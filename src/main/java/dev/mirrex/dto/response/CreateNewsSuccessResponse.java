package dev.mirrex.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNewsSuccessResponse {

    private Long id;

    private Integer statusCode = 1;

    private Boolean success = true;
}
