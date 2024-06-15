package com.ads.jobsjop20.model;

import java.io.Serializable;

public class CEPResponse implements Serializable {

    private String cep;
    private String localidade;
    private String uf;

    public CEPResponse() {
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
