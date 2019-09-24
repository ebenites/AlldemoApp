package pe.ebenites.alldemo.models;

import java.util.Objects;

public class Gender {

    private String id;

    private String name;

    public Gender() {
    }

    public Gender(String id, String nombre) {
        this.id = id;
        this.name = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gender sexo = (Gender) o;
        return Objects.equals(id, sexo.id);
    }


}
