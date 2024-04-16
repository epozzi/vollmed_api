package med.voll.api.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(EntityNotFoundException.class) // JPA não encontrou no banco
    public ResponseEntity tratarErro500() {
        return ResponseEntity.notFound().build(); //devolve erro 404
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Bean Validation encontrou algum campo inválido
    public ResponseEntity tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();

        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    // Como esse dto é específico para o tratamento de erro, foi criado como privado dentro da própria classe de erro.
    private record DadosErroValidacao(
            String campo,
            String mensagem
    ) {

        public DadosErroValidacao (FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}
