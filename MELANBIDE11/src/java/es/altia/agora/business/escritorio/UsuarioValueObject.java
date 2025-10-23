package es.altia.agora.business.escritorio;

/**
 * Stub implementation of UsuarioValueObject for compilation compatibility
 */
public class UsuarioValueObject {
    
    private String usuario;
    private String nombre;
    private String organizacion;
    private Integer codOrganizacion;
    
    public UsuarioValueObject() {
    }
    
    public String getUsuario() {
        return usuario;
    }
    
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getOrganizacion() {
        return organizacion;
    }
    
    public void setOrganizacion(String organizacion) {
        this.organizacion = organizacion;
    }
    
    public Integer getCodOrganizacion() {
        return codOrganizacion;
    }
    
    public void setCodOrganizacion(Integer codOrganizacion) {
        this.codOrganizacion = codOrganizacion;
    }
}