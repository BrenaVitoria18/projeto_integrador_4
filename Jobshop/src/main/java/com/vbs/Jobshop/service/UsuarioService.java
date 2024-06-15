package com.vbs.Jobshop.service;

import com.vbs.Jobshop.model.UsuarioModel;
import com.vbs.Jobshop.repository.UsuarioRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {
     @Autowired
     private UsuarioRepository usuarioRepository;



     public boolean emailJaCadastrado(String email) {
          Optional<UsuarioModel> usuario = usuarioRepository.findByEmail(email);
          return usuario.isPresent();
     }

     public UsuarioModel salvarUsuario(UsuarioModel usuarioModel) {

          return usuarioRepository.save(usuarioModel);
     }

     public UsuarioModel findUserByEmailAndSenha(String email, String senha) {
          Optional<UsuarioModel> usuarioOptional = usuarioRepository.findByEmailAndSenha(email, senha);


          return usuarioOptional.orElse(null);
     }

     public UsuarioModel atualizaUsuario(Long id, Map<String, Object> atributos) {
          UsuarioModel usuarioModel = usuarioRepository.findById(id)
                  .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

          atributos.forEach((chave, valor) -> {
               switch (chave) {
                    case "nome_usuario":
                         usuarioModel.setNome((String) valor);
                         break;
                    case "email":
                         usuarioModel.setEmail((String) valor);
                         break;
                    case "descricao":
                         usuarioModel.setDescricao((String) valor);
                         break;
                    case "senha":
                         usuarioModel.setSenha((String) valor);
                         break;
                    case "imagemUsuario":
                         usuarioModel.setImagemUsuario((String) valor);
                         break;
                    case "cep":
                         usuarioModel.setCep((String) valor);
                         break;
                    case "estado":
                         usuarioModel.setEstado((String)valor);
                         break;
                    case "cidade":
                         usuarioModel.setCidade((String) valor);
                         break;
                    case "numero":
                         usuarioModel.setNumero((String) valor);
                         break;

                    default:
                         throw new IllegalArgumentException("Atributo desconhecido: " + chave);
               }
          });

          return usuarioRepository.save(usuarioModel);
     }

}