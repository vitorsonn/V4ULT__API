package com.api.v4ult.modules.video.dto;

import java.util.List;

public record PageResponseDTO<T>(List<T> content,
                                 int page,
                                 int size,
                                 long totalElements) {
}
