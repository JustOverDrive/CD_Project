/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package curriculodigitalv2;

import blockchain.utils.SecurityUtils;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

/**
 *
 * @author Asus
 */
public class Curriculo implements Serializable{
    
    private String instituto;
    private String institutoPub;
    
    private String pessoa;
    private String pessoaPub;

    private List<String> eventos;
    private String assinatura;
    
    public Curriculo(String pessoa, List<String> eventos){
        this.pessoa = pessoa;
        this.eventos = eventos;
    }

    public Curriculo(String instituto, String pessoa, List<String> eventos) {
        this.instituto = instituto;
        this.pessoa = pessoa;
        this.eventos = eventos;
    }

    public Curriculo(User instituto, User pessoa, List<String> eventos) throws Exception {
        this.instituto = instituto.getName();
        this.institutoPub = Base64.getEncoder().encodeToString(instituto.getPubKey().getEncoded());
        this.pessoa = pessoa.getName();
        this.pessoaPub = Base64.getEncoder().encodeToString(pessoa.getPubKey().getEncoded());
        this.eventos = eventos;
        sign(instituto.getPrivKey());
    }

    public void sign(PrivateKey priv) throws Exception {
        byte[] dataSign = SecurityUtils.sign(
                (institutoPub + pessoaPub + eventos).getBytes(),
                priv);
        this.assinatura = Base64.getEncoder().encodeToString(dataSign);
    }

    public boolean isValid() {
        try {
            PublicKey pub = SecurityUtils.getPublicKey(Base64.getDecoder().decode(institutoPub));
            byte[] data = (institutoPub + pessoaPub + eventos).getBytes();
            byte[] sign = Base64.getDecoder().decode(assinatura);
            return SecurityUtils.verifySign(data, sign, pub);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%-10s -> %-10s %s -> %s", instituto, pessoa, isValid() + "", eventos);
        //return from + "\t : " + to + "\t -> " + value;
    }

    public String getInstituto() {
        return instituto;
    }

    public void setInstituto(String instituto) {
        this.instituto = instituto;
    }

    public String getInstitutoPub() {
        return institutoPub;
    }

    public void setInstitutoPub(String institutoPub) {
        this.institutoPub = institutoPub;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getPessoaPub() {
        return pessoaPub;
    }

    public void setPessoaPub(String pessoaPub) {
        this.pessoaPub = pessoaPub;
    }

    public List<String> getEventos() {
        return eventos;
    }

    public void setEventos(List<String> eventos) {
        this.eventos = eventos;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }
    
}
