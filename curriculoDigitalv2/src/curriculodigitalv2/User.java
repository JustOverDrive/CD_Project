/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package curriculodigitalv2;

import blockchain.utils.SecurityUtils;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author Asus
 */
public class User {
    
    public static String FOLDER = "dados";
    
    private String name;
    private Integer type;
    
    private PublicKey pubKey;
    private PrivateKey privKey;
    private Key simKey;
    
    public User(String nome, Integer type) throws Exception{
        this.name = nome;
        this.type = type;
        this.pubKey = null;
        this.privKey = null;
        this.simKey = null;
    }
    
    public User(String nome) throws Exception{
        this.name = nome;
        this.type = null;
        this.pubKey = null;
        this.privKey = null;
        this.simKey = null;
    }
    
    public User(){
        this.name = "noName";
        this.type = null;
        this.pubKey = null;
        this.privKey = null;
        this.simKey = null;
    }
    
    public static void createFolder(){
        File folder = new File(FOLDER);
        if(!folder.exists()){
            folder.mkdir();
        }
    }
    
    public void generateKeys() throws Exception {
        this.simKey = SecurityUtils.generateAESKey(256);
        
        KeyPair kp = SecurityUtils.generateECKeyPair(256);
        
        this.pubKey = kp.getPublic();
        this.privKey = kp.getPrivate();
    }
    
    public void save(String password) throws Exception{
        byte[] type = ByteBuffer.allocate(4).putInt(this.type).array();
        Files.write(Path.of(FOLDER).resolve(this.name + ".type"), type);
        //encriptar a chave privada
        byte[] secret = SecurityUtils.encrypt(privKey.getEncoded(), password);
        Files.write(Path.of(FOLDER).resolve(this.name + ".priv"), secret);
        //encrptar a chave simetrica
        byte[] simData = SecurityUtils.encrypt(simKey.getEncoded(), password);
        Files.write(Path.of(FOLDER).resolve(this.name + ".sim"), simData);
        //guardar a public
        Files.write(Path.of(FOLDER).resolve(this.name + ".pub"), pubKey.getEncoded());
    }
    
    public void load(String password) throws Exception {
        //desencriptar a privada
        byte[] privData = Files.readAllBytes(Path.of(FOLDER).resolve(this.name + ".priv"));
        privData = SecurityUtils.decrypt(privData, password);
        //desencriptar a privada
        byte[] simData = Files.readAllBytes(Path.of(FOLDER).resolve(this.name + ".sim"));
        simData = SecurityUtils.decrypt(simData, password);
        //ler a publica
        byte[] pubData = Files.readAllBytes(Path.of(FOLDER).resolve(this.name + ".pub"));
        byte[] typeData = Files.readAllBytes(Path.of(FOLDER).resolve(this.name + ".type"));
        this.type = ByteBuffer.wrap(typeData).getInt();
        this.privKey = SecurityUtils.getPrivateKey(privData);
        this.pubKey = SecurityUtils.getPublicKey(pubData);
        this.simKey = SecurityUtils.getAESKey(simData);
    }
    
    public void loadPublic() throws Exception{
         //ler a publica
        byte[] pubData = Files.readAllBytes(Path.of(this.name + ".pub"));
        this.pubKey = SecurityUtils.getPublicKey(pubData);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PublicKey pubKey) {
        this.pubKey = pubKey;
    }

    public PrivateKey getPrivKey() {
        return privKey;
    }

    public void setPrivKey(PrivateKey privKey) {
        this.privKey = privKey;
    }

    public Key getSimKey() {
        return simKey;
    }

    public void setSimKey(Key simKey) {
        this.simKey = simKey;
    }
}
