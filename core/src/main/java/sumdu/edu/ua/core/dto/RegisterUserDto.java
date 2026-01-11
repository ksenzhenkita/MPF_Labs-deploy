package sumdu.edu.ua.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {

    @Email(message = "Некоректний email")
    @NotBlank(message = "Email обов'язковий")
    private String email;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 6, message = "Пароль має бути не менше 6 символів")
    private String password;

    @NotBlank(message = "Псевдонім не може бути порожнім")
    private String nickname;


}
