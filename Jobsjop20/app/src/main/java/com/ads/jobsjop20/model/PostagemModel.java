package com.ads.jobsjop20.model;

import java.io.Serializable;

public class PostagemModel implements Serializable {
    private Long idPost;
    private String descricaoPost;
    private String imagem;
    private String local;
    private Long idUsuario;
    private String nomeServico;
    private int like;
    private UsuarioModel usuario;

    public PostagemModel() {
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public PostagemModel(String descricaoPost, String imagem, String local, Long idUsuario, UsuarioModel usuario, String nomeServico, int like) {
        this.descricaoPost = descricaoPost;
        this.imagem = imagem;
        this.local = local;
        this.idUsuario = idUsuario;
        this.nomeServico = nomeServico;
        this.usuario = usuario;
        this.like = like;
    }


    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel nomeUsuario) {
        this.usuario = nomeUsuario;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }



    public Long getIdPost() {
        return idPost;
    }

    public void setIdPost(Long idPost) {
        this.idPost = idPost;
    }

    public String getDescricaoPost() {
        return descricaoPost;
    }

    public void setDescricaoPost(String descricaoPost) {
        this.descricaoPost = descricaoPost;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
}
