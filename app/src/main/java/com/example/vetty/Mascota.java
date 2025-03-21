package com.example.vetty;

import java.util.HashMap;
import java.util.Map;

public class Mascota {
    private String especie;
    private String nombre;
    private String edad;
    private String raza;
    private String sexo;

    public Mascota() {} // Constructor vacío requerido por Firebase

    public Mascota(String especie, String nombre, String edad, String raza, String sexo) {
        this.especie = especie;
        this.nombre = nombre;
        this.edad = edad;
        this.raza = raza;
        this.sexo = sexo;
    }


    public String getEspecie() { return especie; }
    public String getNombre() { return nombre; }
    public String getEdad() { return edad; }
    public String getRaza() { return raza; }
    public String getSexo() { return sexo; }

    // Setters (permiTTE edición si es necesario)
    public void setEspecie(String especie) { this.especie = especie; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEdad(String edad) { this.edad = edad; }
    public void setRaza(String raza) { this.raza = raza; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    // Conversión a Map para Firebasea
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("especie", especie);
        map.put("nombre", nombre);
        map.put("edad", edad);
        map.put("raza", raza);
        map.put("sexo", sexo);
        return map;
    }
}
