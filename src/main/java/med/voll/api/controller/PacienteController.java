package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.domain.paciente.PacienteRepository;
import med.voll.api.domain.paciente.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository repository;

    /*
     * Para requisições via HTTP ou HTTPs existem padrões de respostas.
     * No SpringBoot as respostas então padronizadas dentro do ResponseEntity.
     * Para cadastrar (POST):
     *   -> Padrão de resposta:
     *       * Código 201
     *       * No corpo da resposta devolve os dados cadastrados
     *       * No header devolve a URL no serviço para detalhar os dados cadastrados
     *
     * Para consultar (GET):
     *   -> Padrão de resposta:
     *       * Código 200
     *       * No corpo da resposta devolve os dados da consulta
     *
     * Para atualizar (PUT):
     *   -> Padrão de resposta:
     *       * Código 200
     *       * No corpo da resposta devolve os dados atualizados
     *
     * Para excluir (DELETE)
     *  -> Padrão de resposta:
     *       * Código 204
     *       * Corpo da resposta sem conteúdo
     * */

    @PostMapping
    @Transactional
    public ResponseEntity cadastar(
            @RequestBody @Valid DadosCadastroPaciente dados,
            UriComponentsBuilder uriBuilder
    ){
        Paciente paciente = new Paciente(dados);
        repository.save(paciente);

        URI uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoPaciente(paciente));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemPaciente>> listar(
            @PageableDefault(size=10, sort = {"nome"}) Pageable paginacao
    ){
        return ResponseEntity.ok(
                repository.findAllByAtivoTrue(paginacao).map(DadosListagemPaciente::new)
        );
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualiza(@RequestBody @Valid DadosAtualizacaoPaciente dados) {
        Paciente paciente = repository.getReferenceById(dados.id());
        paciente.atuzalizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoPaciente(paciente));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        Paciente paciente = repository.getReferenceById(id);
        paciente.excluir();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        Paciente paciente = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoPaciente(paciente));
    }

}
