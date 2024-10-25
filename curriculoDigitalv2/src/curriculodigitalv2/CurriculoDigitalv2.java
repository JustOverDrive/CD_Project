package curriculodigitalv2;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import blockchain.utils.MerkleTree;
import blockchain.utils.ObjectUtils;
import static curriculodigitalv2.User.FOLDER;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiago Ferreira
 */
public class CurriculoDigitalv2 implements Serializable{
    
    private ArrayList<Curriculo> curriculoArrayList;

    private BlockChain bc;
    
    private static int DIFFICULTY = 3;
    
    public CurriculoDigitalv2(){
        curriculoArrayList = new ArrayList<>();
        bc = new BlockChain();
        
        loadCurriculum();
    }
    
    
    public void addCurriculo(Curriculo curriculo) throws Exception{
        List<Object> dados = new ArrayList<>();
        
        dados.add(ObjectUtils.convertObjectToBase64(curriculo));
        
        dados.add(curriculo.isValid());
        
        MerkleTree mt = new MerkleTree(dados);
        
        bc.add(mt.getRoot(), DIFFICULTY);
        
        mt.saveToFile(FOLDER + "/" + bc.getLastBlockHash() + ".mkt");
        
        curriculoArrayList.add(curriculo);
        
        saveCurriculum();
    }
    
    
    public void saveBC() throws Exception{
        bc.save(FOLDER + "/" + "blockchain.bc");
    }
    
    public void loadBC(){
        try {
            bc.load(FOLDER + "/" + "blockchain.bc");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    public String blockChainToString() throws Exception {
        StringBuilder txt = new StringBuilder();
        // Itera por todos os blocos na blockchain
        for( Block b : bc.getChain()){
            // Carregar a Merkle Tree correspondente ao bloco
            MerkleTree mt = MerkleTree.loadFromFile(FOLDER + "/" + b.getCurrentHash() + ".mkt");
            
            // Recuperar o currículo a partir dos elementos da Merkle Tree
            String base64 = (String) mt.getElements().get(0); // Primeiro elemento é o currículo
            
            // Converte o currículo de Base64 para um objeto Curriculo
            Curriculo c = (Curriculo) ObjectUtils.convertBase64ToObject(base64);
            
            // Adiciona ao texto a hash anterior, o currículo, o nonce e o hash atual
            txt.append(
                    b.getPreviousHash() + " "  // Hash anterior
                    + c.toString() + " "       // Curriculo
                    + b.getNonce() +" "        // Nounce
                    + b.getCurrentHash()       // Hash atual
                    +"\n"
            );
        }
        // Retorna a representação em string da blockchain
        return txt.toString();
    }
    
    private void saveCurriculum() throws Exception{
        try (PrintStream out = new PrintStream(new FileOutputStream(FOLDER + "/" + "curriculos.txt"))) {
            for (Curriculo curriculo : curriculoArrayList) {
                out.println(curriculo.getPessoa() + ':' + String.join(",", curriculo.getEventos()));
            }
        } catch (Exception e) {
            System.out.println("Erro ao guardar o Curriculum");
        }
    }
    
    public void loadCurriculum(){
        try(Scanner scanner = new Scanner(new FileInputStream(FOLDER + "/" + "curriculos.txt"))){
            // Enquanto há mais linhas para ler... continuar a ler
            while(scanner.hasNextLine()){
                // Lê a próxima linha
                String linha = scanner.nextLine();
                // Divide a linha por ":"
                String[] partes = linha.split(":");
                // Confirma que a linha foi dividida corretamente
                if(partes.length == 2) {
                   // A parte do nome da pessoa
                   String nomePessoa = partes[0];
                   // A parte dos eventos dividida por ","
                   String[] eventos = partes[1].split(",");
                   // Cria um novo Curriculo
                   Curriculo curriculo = new Curriculo(nomePessoa, Arrays.asList(eventos));
                   // Adiciona o currículo à lista de currículos
                   curriculoArrayList.add(curriculo);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar o Curriculum");
        }
    }
    
    public Set<String> getListPessoas(){
        Set<String> setPessoas = new HashSet<>();
        for(Curriculo curriculo : curriculoArrayList){
            setPessoas.add(curriculo.getPessoa());
        }
        return setPessoas;
    }
    
    public List<Curriculo> getTransactionBlockchain() throws Exception{
        List<Curriculo> lst = new ArrayList<>();
        for(Block b : bc.getChain()){
            MerkleTree mt = MerkleTree.loadFromFile(FOLDER + "/" + b.getCurrentHash() + ".mkt");
            
            String base64 = (String) mt.getElements().get(0);
            
            Curriculo c = (Curriculo) ObjectUtils.convertBase64ToObject(base64);
            lst.add(c);
        }
        return lst;
    }
}
