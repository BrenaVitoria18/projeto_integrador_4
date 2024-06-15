package com.vbs.Jobshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import java.util.*;

@Table(name = "Usuario")
@Entity
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idUsuario")
public class UsuarioModel  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    private String nome;
    private String email;
    private String descricao;
    private String senha;
    private String imagemUsuario;
    private String cep;
    private String estado;
    private String cidade;
    private String numero;



    @OneToMany(mappedBy = "usuario")
    @JsonIgnoreProperties("usuario-post")
    private List<PostModel> posts;



    public UsuarioModel(Long idUsuario){

       this.idUsuario = idUsuario;
    }


}
