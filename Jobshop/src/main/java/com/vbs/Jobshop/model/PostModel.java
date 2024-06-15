package com.vbs.Jobshop.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;


@Table(name = "Post")
@Entity
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idPost")
public class PostModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPost;
    private String descricaoPost;
    private String imagem;
    private String nomeServico;
    private String local;
    private int likes = 0;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    @JsonIgnoreProperties("posts")
    private UsuarioModel usuario;


}
