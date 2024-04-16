package med.voll.api.domain.medico;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import med.voll.api.domain.endereco.DadosEndereco;

public record DadosCadastroMedico(

        @NotBlank(message = "Nome é obrigatório") // anotação para o bean validation que o campo não pode ser nulo
        String nome,
        @NotBlank(message = "E-mail é obrigatório")
        @Email
        String email,
        // colocando esse parametro message conseguie personalizar a mensagem de erro
        // para cada campo obrigatório na validação do Bean Validation
        @NotBlank(message = "Telefone é obrigatório")
        String telefone,
        // também pode colocar as mensagens em um arquivo de configurações que fica no resources do projeto
        // ValidationMessages.properties
        @NotBlank(message = "{crm.obrigatorio}")
        @Pattern(regexp = "\\d{4,6}")
        String crm,
        @NotNull
        Especialidade especialidade,
        @NotNull
        @Valid
        DadosEndereco endereco) {
}
