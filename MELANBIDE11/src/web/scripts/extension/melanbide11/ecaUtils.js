(function(global){
  // Utilidades mínimas para compatibilidad con pantallas ECA.
  var ecaUtils = {
    // Formatea número con separador decimal coma y miles con puntos.
    formatEuro: function(n){
      if (n==null || isNaN(+n)) return '0,00';
      var parts = (+n).toFixed(2).split('.');
      parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, '.');
      return parts.join(',');
    },
    escapeHtml: function(s){
      if(!s && s!==0) return '';
      return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#39;');
    }
  };
  global.ecaUtils = ecaUtils;
})(window);

$(document).ready(function() {
    function calcularClienteComputable() {
        var salario = parseFloat($('#salarioBase').val()) || 0;
        var pagas = parseFloat($('#pagasExtraordinarias').val()) || 0;
        var sumaFijos = 0;
        $('#tablaComplementosSalariales tbody tr').each(function() {
            var tipo = $(this).find('.tipoComplemento').val();
            var imp = parseFloat($(this).find('.importeComplemento').val()) || 0;
            if (tipo && (tipo.toUpperCase() === 'FIJO' || tipo.toUpperCase() === 'FINKOA')) {
                sumaFijos += imp;
            }
        });
        var total = salario + pagas + sumaFijos;
        $('#retribucionComputable').val(total.toFixed(2));
        return total;
    }

    function calcularClienteBruta() {
        var salario = parseFloat($('#salarioBase').val()) || 0;
        var pagas = parseFloat($('#pagasExtraordinarias').val()) || 0;
        var sumaAll = 0;
        $('#tablaComplementosSalariales tbody tr').each(function() {
            var imp = parseFloat($(this).find('.importeComplemento').val()) || 0;
            sumaAll += imp;
        });
        $('#tablaOtrasPercepciones tbody tr').each(function() {
            var imp = parseFloat($(this).find('.importePercepcion').val()) || 0;
            sumaAll += imp;
        });
        var total = salario + pagas + sumaAll;
        $('#CSTCONT').val(total.toFixed(2));
        return total;
    }

    function sincronizarBackend(idContratacion) {
        if (!idContratacion) return;
        $.ajax({
            url: 'melanbide11Action.do',
            type: 'POST',
            data: {
                action: 'recalcularRetribuciones',
                idContratacion: idContratacion
            },
            success: function(response) {
                try {
                    if (response.success) {
                        $('#retribucionComputable').val(response.retribucionComputable);
                        $('#CSTCONT').val(response.retribucionBrutaTotal);
                    } else {
                        console.error('Error backend:', response.error);
                    }
                } catch (e) { console.error(e); }
            },
            error: function() { console.error('Error AJAX recalcularRetribuciones'); }
        });
    }

    $(document).on('change keyup', '#salarioBase, #pagasExtraordinarias, .importeComplemento, .tipoComplemento, .importePercepcion', function() {
        calcularClienteComputable();
        calcularClienteBruta();
        // opcional: sincronizarBackend($('#idContratacion').val());
    });

    // inicial
    calcularClienteComputable();
    calcularClienteBruta();
});
