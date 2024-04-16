package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired // injeção da dependencia para o spring assim ele sabe que é ele quem vai controlar a instância desse objeto
    private MedicoRepository repository;

    @PostMapping
    @Transactional // Quando atualiza informações no BD precisa da anotação Transational
    public ResponseEntity cadastrar(
            @RequestBody @Valid DadosCadastroMedico dados,
            UriComponentsBuilder uriBuilder
    ) {
        // Resposta padrão para métodos post é 201 e deve conter as informações no corpo da resposta e ainda um cabeçaljo
        var medico = new Medico(dados);
        repository.save(medico);

        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemMedico>> listar(
            @PageableDefault(size=10, sort = {"nome"}) //Define parâmetros default que podem ser sobrescrevidos pela URL
            Pageable paginacao //atenção para não importar o Pageble do java.utils
    ) { //paginação com spring
        return ResponseEntity.ok(repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
        // Por ter anotado o método como transação a JPA já entende que o put vai atualizar o registro instanciado
        // Então só é preciso instanciar o registro e assim que as atualizações forem feitas nele a JPA já atualiza
        // coisa de maluco isso
        Medico medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        Medico medico = repository.getReferenceById(id);
        medico.excluir();

        return ResponseEntity.noContent().build(); // Reposta para delete é 204 - ok noContent
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        Medico medico = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }
}

//Os códigos HTTP (ou HTTPS) possuem três dígitos, sendo que o primeiro dígito significa a classificação dentro das possíveis cinco categorias.
//
//1XX: Informativo – a solicitação foi aceita ou o processo continua em andamento;
//
//2XX: Confirmação – a ação foi concluída ou entendida;
//
//3XX: Redirecionamento – indica que algo mais precisa ser feito ou precisou ser feito para completar a solicitação;
//
//4XX: Erro do cliente – indica que a solicitação não pode ser concluída ou contém a sintaxe incorreta;
//
//5XX: Erro no servidor – o servidor falhou ao concluir a solicitação.



