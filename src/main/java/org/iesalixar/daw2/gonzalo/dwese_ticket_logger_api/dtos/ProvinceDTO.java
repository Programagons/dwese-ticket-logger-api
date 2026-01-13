package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase DTO (Data Transfer Object) que representa una provincia.
 *
 * Esta clase se utiliza para transferir datos de una provincia
 * entre las capas de la aplicación, especialmente para exponerlos
 * a través de la API sin incluir información innecesaria o sensible.
 *
 */
@Getter
@Setter
public class ProvinceDTO {
    private Long id;
    private String code;
    private String name;
    private RegionDTO region; // Información básica de la región

}
