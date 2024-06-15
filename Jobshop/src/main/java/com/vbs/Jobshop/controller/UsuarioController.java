package com.vbs.Jobshop.controller;

import com.vbs.Jobshop.model.UsuarioModel;
import com.vbs.Jobshop.repository.UsuarioRepository;
import com.vbs.Jobshop.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UsuarioController {
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioService user_service;

    @GetMapping(value = "/usuario", produces = "application/json")
    public ResponseEntity<Object> ListaTodosUsuarios(){
        List<UsuarioModel> usuarioModelList = repository.findAll();
        return ResponseEntity.ok(usuarioModelList);
    }

    @GetMapping(value = "/usuario/{id}", produces = "application/json")
    public ResponseEntity<UsuarioModel> listaUsuarioId(@PathVariable("id") Long id){
        Optional<UsuarioModel> usuarioModelOptional = repository.findById(id);
        return usuarioModelOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping(value = "/usuario", produces = "application/json")
    public ResponseEntity<Object> salvaUsuario(@RequestBody UsuarioModel usuarioModel){
        if (user_service.emailJaCadastrado(usuarioModel.getEmail())) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        UsuarioModel usuarioCadastrado = user_service.salvarUsuario(usuarioModel);
        return new ResponseEntity<>(usuarioCadastrado, HttpStatus.CREATED);
    }

    @PutMapping(value = "/usuario/{id}", produces = "application/json")
    public ResponseEntity<Object> atualizaUsuario(@PathVariable Long id, @RequestBody Map<String, Object> atributos) {
        UsuarioModel usuario = user_service.atualizaUsuario(id, atributos);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) {
        Optional<UsuarioModel> usuarioModel = repository.findById(id);
        if(usuarioModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }
        repository.delete(usuarioModel.get());
        return ResponseEntity.ok("Usuário deletado com sucesso");
    }

    @GetMapping("/usuario/exists")
    public ResponseEntity<Object> verificarUsuario(@RequestParam String email,
                                                   @RequestParam String senha) {
        UsuarioModel usuarioModel = user_service.findUserByEmailAndSenha(email, senha);
        if (usuarioModel != null) {
            return ResponseEntity.ok(usuarioModel);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
    }
}
