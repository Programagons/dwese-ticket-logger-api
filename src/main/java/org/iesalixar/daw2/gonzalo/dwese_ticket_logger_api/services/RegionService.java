package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.services;

import jakarta.validation.Valid;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos.RegionCreateDTO;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos.RegionDTO;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.entities.Region;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.mappers.RegionMapper;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.repositories.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class RegionService {

    private static final Logger logger = LoggerFactory.getLogger(RegionService.class);

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Obtiene todas las regiones con paginación y las convierte en una página de RegionDTO.
     *
     * @param pageable Objeto de paginación que define la página, el tamaño y la ordenación.
     * @return Página de RegionDTO
     *
     */

    public Page<RegionDTO> getAllRegions(Pageable pageable) {
        logger.info("Solicitando todas las regiones con paginación: página {}, tamaño{}",
            pageable.getPageNumber(), pageable.getPageSize());
        try{
            Page<Region> regions = regionRepository.findAll(pageable);
            logger.info("Se han encontrado {} regiones en la página actual.", regions.getNumberOfElements());
            return regions.map(regionMapper::toDTO);
        } catch (Exception e) {
            logger.error("Error al obtener la lista paginada de regiones: {}", e.getMessage());
            throw e;
        }
    }


    /**
     * Busca una región específica por su ID.
     *
     * @param id Identificador único de la region.
     * @return Un Optional que contiene un 'RegionDTO' si la región existe.
     */

    public Optional<RegionDTO> getRegionById(Long id) {
        try {
            logger.info("Buscando región con ID {}...");
            return regionRepository.findById(id).map(regionMapper::toDTO);
        } catch (Exception e) {
            logger.error("Error al buscar región con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al buscar la región.", e);

        }
    }

    /**
     *
     * Crea una nueva región en la base de datos.
     *
     * @param regionCreateDTO DTO que contiene los datos de la región a crear.
     * @param locale          Idioma para los mensajes de error.
     * @return Respuesta HTTP con el estado de la operación.
     * @throws IllegalArgumentException Si el código ya existe.
     *
     */

    public RegionDTO createRegion(RegionCreateDTO regionCreateDTO, Locale locale) {
        if (regionRepository.existsRegionByCode(regionCreateDTO.getCode())) {
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
            throw new IllegalArgumentException(errorMessage);
        }


        // Procesar la imagen si se proporciona
        String fileName = null;
       /*if (regionCreateDTO.getImageFile() != null && !regionCreateDTO.getImageFile().isEmpty()){
            fileName = fileStorageService.saveFile(regionCreateDTO.getImageFile());
            if (fileName == null) {
                throw new RuntimeException("Error al guardar la imagen.");
            }
        }*/

        // Se convierte a Entity para almacenar en la base de datos
        Region region = regionMapper.toEntity(regionCreateDTO);
        region.setImage(fileName);

        Region savedRegion = regionRepository.save(region);
        logger.info("Región creada exitosamente con ID {}", savedRegion.getId());
        // Se devuelve el DTO
        return regionMapper.toDTO(savedRegion);
    }


    /**
     * @param id              Identificador de la región a actualizar.
     * @param regionCreateDTO DTO que contiene los nuevos datos de la región.
     * @param locale          idioma para los mensajes de error.
     * @return DTO de la región actualizada.
     * @throws IllegalArgumentException Si la región no existe o el código ya está en uso.
     *
     */

    public RegionDTO updateRegion(Long id, RegionCreateDTO regionCreateDTO, Locale locale) {
        logger.info("Actualizando región con ID {}", id);
        // Buscar la región existente
        Region existingRegion = regionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La región no existe."));

        if (regionRepository.existsRegionByCodeAndNotId(regionCreateDTO.getCode(), id)){
            String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
            throw new IllegalArgumentException(errorMessage);
        }

        // Procesar la imagen si se proporciona
        String fileName = existingRegion.getImage(); // Conservar la imagen existente por defecto.
        /*if (regionCreateDTO.getImageFile() != null && !regionCreateDTO.getImageFile().isEmpty()){
            fileName = fileStorageService.saveFile(regionCreateDTO.getImageFile());
            if (fileName == null){
                throw new RuntimeException("Error al guardar la imagen.");
            }
        }*/

        // Actualizar los datos de la categoría
        existingRegion.setCode(regionCreateDTO.getCode());
        existingRegion.setName(regionCreateDTO.getName());
        existingRegion.setImage(fileName);
        // Guardar los cambios
        Region updatedRegion = regionRepository.save(existingRegion);
        logger.info("Región con ID {} actualizada exitosamente", updatedRegion.getId());

        return regionMapper.toDTO(updatedRegion);
    }

    /**
     * Elimina una región específica por su ID.
     *
     * @param id identificador único de la región.
     * @return IllegalArgumentException Si la región no existe.
     */

    public void deleteRegion(Long id) {
        logger.info("Buscando region con ID {}", id);

        // Buscar la región
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La región no existe."));

        // Eliminar la imagen asociada si existe
        if (region.getImage() != null && !region.getImage().isEmpty()){
            fileStorageService.deleteFile(region.getImage());
            logger.info("Imagen asociada a la región con ID {} eliminada.", id);
        }

        // Eliminar la región
        regionRepository.deleteById(id);
        logger.info("Región con ID {} eliminada exitosamente.", id);
    }
}
