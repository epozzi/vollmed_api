package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired // injeção da dependencia para o spring assim ele sabe que é ele quem vai controlar a instância desse objeto
    private MedicoRepository repository;

    @PostMapping
    @Transactional // Quando atualiza informações no BD precisa da anotação Transational
    public void cadastrar(@RequestBody @Valid DadosCadastroMedico dados) {
        repository.save(new Medico(dados));
    }

    @GetMapping
    public Page<DadosListagemMedico> listar(
            @PageableDefault(size=10, sort = {"nome"}) //Define parâmetros default que podem ser sobrescrevidos pela URL
            Pageable paginacao //atenção para não importar o Pageble do java.utils
    ) { //paginação com spring
        return repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
    }

    @PutMapping
    @Transactional
    public void atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
        // Por ter anotado o método como transação a JPA já entende que o put vai atualizar o registro instanciado
        // Então só é preciso instanciar o registro e assim que as atualizações forem feitas nele a JPA já atualiza
        // coisa de maluco isso
        Medico medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable Long id) {
        Medico medico = repository.getReferenceById(id);
        medico.excluir();
    }
}
