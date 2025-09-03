/**
 * Popup and modal window functionality
 * Provides functions to open modal dialogs and popup windows
 */

// Global variable to store the current modal window reference
var currentModalWindow = null;

/**
 * Opens a modal popup window
 * @param {string} url - URL to load in the popup
 * @param {number} height - Height of the popup window
 * @param {number} width - Width of the popup window  
 * @param {string} scrollbars - Whether to show scrollbars ('yes'/'no')
 * @param {string} resizable - Whether the window is resizable ('yes'/'no')
 * @param {function} callback - Callback function to execute when popup closes
 */
function lanzarPopUpModal(url, height, width, scrollbars, resizable, callback) {
    // Set default values
    height = height || 600;
    width = width || 800;
    scrollbars = scrollbars || 'yes';
    resizable = resizable || 'yes';
    
    // Ensure minimum scrollbars for large forms to prevent display issues
    if (height > 600 || width > 1000) {
        scrollbars = 'yes';
    }
    
    // Constrain to screen size with some margin
    var maxHeight = screen.height * 0.9;
    var maxWidth = screen.width * 0.9;
    
    if (height > maxHeight) height = maxHeight;
    if (width > maxWidth) width = maxWidth;
    
    // Calculate center position
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    
    // Ensure popup doesn't go off screen
    if (left < 0) left = 0;
    if (top < 0) top = 0;
    
    // Build window features string
    var features = 'height=' + height + 
                   ',width=' + width + 
                   ',left=' + left + 
                   ',top=' + top + 
                   ',scrollbars=' + scrollbars + 
                   ',resizable=' + resizable + 
                   ',menubar=no' +
                   ',toolbar=no' +
                   ',location=no' +
                   ',status=no';
    
    try {
        // Open the popup window
        currentModalWindow = window.open(url, '_blank', features);
        
        if (!currentModalWindow) {
            alert('Error: No se pudo abrir la ventana emergente. Verifique que los popups estén habilitados.');
            return;
        }
        
        // Focus the popup window
        currentModalWindow.focus();
        
        // Set up callback handling if provided
        if (callback && typeof callback === 'function') {
            // Create a global function that the popup can call
            window.retornoXanelaAuxiliar = function(result) {
                callback(result);
                // Clean up the global function
                window.retornoXanelaAuxiliar = null;
            };
            
            // Monitor when the popup is closed without callback
            var checkClosed = setInterval(function() {
                if (currentModalWindow && currentModalWindow.closed) {
                    clearInterval(checkClosed);
                    currentModalWindow = null;
                    // Clean up the global function if popup was closed without calling callback
                    if (window.retornoXanelaAuxiliar) {
                        window.retornoXanelaAuxiliar = null;
                    }
                }
            }, 1000);
        }
        
    } catch (e) {
        alert('Error al abrir la ventana: ' + e.message);
    }
}

/**
 * Closes the current modal window
 */
function cerrarPopupModal() {
    if (currentModalWindow && !currentModalWindow.closed) {
        currentModalWindow.close();
        currentModalWindow = null;
    }
}

/**
 * Alternative function name for compatibility
 */
function abrirPopup(url, height, width, scrollbars, resizable) {
    return lanzarPopUpModal(url, height, width, scrollbars, resizable);
}

/**
 * Fallback alert function if jsp_alerta is not defined by the framework
 * @param {string} tipo - Type of alert ('A' for alert, '' for confirm)
 * @param {string} mensaje - Message to display
 * @returns {number} - 1 for OK/Yes, 0 for Cancel/No
 */
if (typeof jsp_alerta === 'undefined') {
    function jsp_alerta(tipo, mensaje) {
        if (tipo === 'A' || tipo === 'a') {
            alert(mensaje);
            return 1;
        } else {
            // Confirm dialog
            return confirm(mensaje) ? 1 : 0;
        }
    }
}