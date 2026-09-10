package com.api.v4ult.modules.video.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateVideoDTO(@NotBlank(message = "O título é obrigatório")
                             String title,

                             String description,

                             @NotBlank(message = "A URL do vídeo é obrigatória")
                             String videoUrl,

                             @NotBlank(message = "O ID do autor é obrigatório")
                             String authorId,

                             @NotBlank(message = "O nome do autor é obrigatório")
                             String authorName) {
}
