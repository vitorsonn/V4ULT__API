package com.api.v4ult.modules.video.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentDTO(@NotBlank(message = "O ID do usuário é obrigatório")
                               String userId,

                               @NotBlank(message = "O nome do usuário é obrigatório")
                               String username,

                               @NotBlank(message = "O texto do comentário não pode estar vazio")
                               String text) {
}
