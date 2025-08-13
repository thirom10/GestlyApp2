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
import java.util.Date

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
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
    val successMessage: String? = null
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
    
    init {
        loadProducts()
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.getProducts().collect { products ->
                    _uiState.value = _uiState.value.copy(
                        products = products,
                        isLoading = false
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
    
    fun searchProducts(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        if (query.isBlank()) {
            loadProducts()
            return
        }
        
        viewModelScope.launch {
            try {
                repository.searchProducts(query).collect { products ->
                    _uiState.value = _uiState.value.copy(products = products)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
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
                    name = state.name,
                    purchasePrice = state.purchasePrice.toDouble(),
                    sellingPrice = state.sellingPrice.toDouble(),
                    stock = state.stock.toInt(),
                    netWeight = state.netWeight.toDouble(),
                    weightUnit = state.weightUnit,
                    branch = state.branch,
                    purchaseDate = state.purchaseDate
                )
                
                repository.addProduct(product).fold(
                    onSuccess = {
                        _addProductUiState.value = AddProductUiState(
                            successMessage = "Producto agregado exitosamente"
                        )
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
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
        _addProductUiState.value = _addProductUiState.value.copy(errorMessage = null)
    }
    
    fun clearSuccess() {
        _addProductUiState.value = _addProductUiState.value.copy(successMessage = null)
    }
}