package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación de una notificación.
 * No incluye el ID ni la fecha de creación, ya que estos son generados automáticamente.
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateDTO {
    private String subject;
    private String message;
    private boolean read;
}
