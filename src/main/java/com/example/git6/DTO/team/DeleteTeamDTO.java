package com.example.git6.DTO.team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DeleteTeamDTO {
    @Schema(description = "团队ID",required = true)
    private List<Long> ids;
}
