package com.example.gestlyapp.ui.feactures.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestlyapp.data.model.Product
import com.example.gestlyapp.data.model.WeightUnit
import com.example.gestlyapp.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.Date

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val hasMoreProducts: Boolean = true,
    val currentPage: Int = 0
)

data class AddProductUiState(
    val name: String = "",
    val purchasePrice: String = "",
    val sellingPrice: String = "",
    val stock: String = "",
    val netWeight: String = "",
    val weightUnit: WeightUnit = WeightUnit.MG,
    val branch: String = "",
    val purchaseDate: Date? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isEditMode: Boolean = false,
    val editingProductId: String? = null
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                purchasePrice.isNotBlank() &&
                sellingPrice.isNotBlank() &&
                stock.isNotBlank() &&
                netWeight.isNotBlank() &&
                branch.isNotBlank()
}

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()
    
    private val _addProductUiState = MutableStateFlow(AddProductUiState())
    val addProductUiState: StateFlow<AddProductUiState> = _addProductUiState.asStateFlow()
    
    // Caché local para productos
    private var cachedProducts: List<Product> = emptyList()
    private var searchJob: Job? = null
    private val pageSize = 20
    
    init {
        loadProducts()
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                currentPage = 0,
                hasMoreProducts = true
            )
            try {
                repository.getProducts().collect { allProducts ->
                    cachedProducts = allProducts
                    val paginatedProducts = allProducts.take(pageSize)
                    _uiState.value = _uiState.value.copy(
                        products = paginatedProducts,
                        isLoading = false,
                        hasMoreProducts = allProducts.size > pageSize,
                        currentPage = 1
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar productos: ${e.message}"
                )
            }
        }
    }
    
    fun loadMoreProducts() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMoreProducts) return
        
        _uiState.value = currentState.copy(isLoadingMore = true)
        
        viewModelScope.launch {
            try {
                val nextPage = currentState.currentPage
                val startIndex = nextPage * pageSize
                val endIndex = minOf(startIndex + pageSize, cachedProducts.size)
                
                if (startIndex < cachedProducts.size) {
                    val newProducts = cachedProducts.subList(startIndex, endIndex)
                    val updatedProducts = currentState.products + newProducts
                    
                    _uiState.value = currentState.copy(
                        products = updatedProducts,
                        isLoadingMore = false,
                        hasMoreProducts = endIndex < cachedProducts.size,
                        currentPage = nextPage + 1
                    )
                } else {
                    _uiState.value = currentState.copy(
                        isLoadingMore = false,
                        hasMoreProducts = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoadingMore = false,
                    errorMessage = "Error al cargar más productos: ${e.message}"
                )
            }
        }
    }
    
    fun searchProducts(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        // Cancelar búsqueda anterior
        searchJob?.cancel()
        
        if (query.isBlank()) {
            loadProducts()
            return
        }
        
        // Debounce de 300ms para optimizar rendimiento
        searchJob = viewModelScope.launch {
            delay(300)
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.searchProducts(query).collect { products ->
                    _uiState.value = _uiState.value.copy(
                        products = products,
                        isLoading = false,
                        hasMoreProducts = false, // Desactivar paginación en búsqueda
                        currentPage = 0
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error en la búsqueda: ${e.message}"
                )
            }
        }
    }
    
    // Add Product functions
    fun updateName(name: String) {
        _addProductUiState.value = _addProductUiState.value.copy(name = name)
    }
    
    fun updatePurchasePrice(price: String) {
        _addProductUiState.value = _addProductUiState.value.copy(purchasePrice = price)
    }
    
    fun updateSellingPrice(price: String) {
        _addProductUiState.value = _addProductUiState.value.copy(sellingPrice = price)
    }
    
    fun updateStock(stock: String) {
        _addProductUiState.value = _addProductUiState.value.copy(stock = stock)
    }
    
    fun updateNetWeight(weight: String) {
        _addProductUiState.value = _addProductUiState.value.copy(netWeight = weight)
    }
    
    fun updateWeightUnit(unit: WeightUnit) {
        _addProductUiState.value = _addProductUiState.value.copy(weightUnit = unit)
    }
    
    fun updateBranch(branch: String) {
        _addProductUiState.value = _addProductUiState.value.copy(branch = branch)
    }
    
    fun updatePurchaseDate(date: Date?) {
        _addProductUiState.value = _addProductUiState.value.copy(purchaseDate = date)
    }
    
    fun addProduct() {
        val state = _addProductUiState.value
        if (!state.isFormValid) return
        
        viewModelScope.launch {
            _addProductUiState.value = _addProductUiState.value.copy(isLoading = true)
            
            try {
                val product = Product(
                    id = state.editingProductId!!, // El ID es necesario para updateProduct
                    name = state.name,
                    purchasePrice = state.purchasePrice.toDouble(),
                    sellingPrice = state.sellingPrice.toDouble(),
                    stock = state.stock.toInt(),
                    netWeight = state.netWeight.toFloat(),
                    weightUnit = state.weightUnit,
                    branch = state.branch,
                    purchaseDate = state.purchaseDate?.time // Usar .time para convertir Date a Long
                )
                
                repository.addProduct(product).fold(
                    onSuccess = {
                        _addProductUiState.value = AddProductUiState(
                            successMessage = "Producto agregado exitosamente"
                        )
                        // Recargar productos para actualizar caché
                        loadProducts()
                    },
                    onFailure = { error ->
                        _addProductUiState.value = _addProductUiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al agregar producto: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _addProductUiState.value = _addProductUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun loadProductForEdit(productId: String) {
        val product = _uiState.value.products.find { it.id == productId }
        if (product != null) {
            _addProductUiState.value = AddProductUiState(
                name = product.name,
                purchasePrice = product.purchasePrice.toString(),
                sellingPrice = product.sellingPrice.toString(),
                stock = product.stock.toString(),
                netWeight = product.netWeight.toString(),
                weightUnit = product.weightUnit,
                branch = product.branch,
                purchaseDate = product.purchaseDate?.let { Date(it) },
                isEditMode = true,
                editingProductId = product.id
            )
        }
    }

    fun updateProduct() {
        val state = _addProductUiState.value
        if (!state.isFormValid || !state.isEditMode || state.editingProductId == null) return
        
        viewModelScope.launch {
            _addProductUiState.value = _addProductUiState.value.copy(isLoading = true)
            
            try {
                val product = Product(
                    name = state.name,
                    purchasePrice = state.purchasePrice.toDouble(),
                    sellingPrice = state.sellingPrice.toDouble(),
                    stock = state.stock.toInt(),
                    netWeight = state.netWeight.toFloat(),
                    weightUnit = state.weightUnit,
                    branch = state.branch,
                    purchaseDate = state.purchaseDate as Long?
                )
                
                repository.updateProduct(product).fold(
                    onSuccess = {
                        _addProductUiState.value = AddProductUiState(
                            successMessage = "Producto actualizado exitosamente"
                        )
                        // Recargar productos para actualizar caché
                        loadProducts()
                    },
                    onFailure = { error ->
                        _addProductUiState.value = _addProductUiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al actualizar producto: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _addProductUiState.value = _addProductUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearForm() {
        _addProductUiState.value = AddProductUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
        _addProductUiState.value = _addProductUiState.value.copy(errorMessage = null)
    }
    
    fun clearSuccess() {
        _addProductUiState.value = _addProductUiState.value.copy(successMessage = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}