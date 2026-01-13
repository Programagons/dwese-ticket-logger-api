package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.controllers;


import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.entities.Province;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.repositories.ProvinceRepository;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.services.FileStorageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controlador REST que maneja las operaciones CRUD para la entidad 'Province'.
 * Expone endpoints para gestionar provincias mediante peticiones HTTP.
 */
@RestController
@RequestMapping("/api/provinces") // prefijo común para todas las rutas del controlador
public class ProvinceController {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);
    public int currentPage = 1;
    public String sort = "idAsc";
    public String search = "";
    @Autowired
    private MessageSource messageSource;
    // DAO para gestionar las operaciones de las provincias en la base de datos
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private FileStorageService fileStorageService;

    /**
     *
     * Lista todas las provincias almacenadas en la base de datos.
     *
     * @return ResponseEntity con la lista de provincias o un error en caso de fallo.
     */
    @GetMapping()
    public ResponseEntity<List<Province>> getAllProvinces() {
        logger.info("Solicitando la lista de todas las provincias...");
        try {
            List<Province> provinces = provinceRepository.findAll();
            logger.info("Se han encontrado {} provincias.", provinces.size());
            return ResponseEntity.ok(provinces);
        } catch (Exception e) {
            logger.error("Error al listar las provincias: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * Obtiene una provincia específica por su ID.
     *
     * @param id ID de la provincia solicitada.
     * @return ResponseEntity con la provincia encontrada o un mensaje de error si no existe.
     */

    @GetMapping("/{id}")
    public ResponseEntity<Province> getProvinceById(@PathVariable Long id) {
        logger.info("Buscando provincia con ID {}", id);
        try {
            Optional<Province> province = provinceRepository.findById(id);
            if (province.isPresent()) {
                logger.info("Provincia con ID {} encontrada: {}", id, province.get());
                return ResponseEntity.ok(province.get());
            } else {
                logger.warn("No se encontró ninguna provincia con ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            logger.error("Error al buscar la provincia con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Muestra el formulario para crear una nueva provincia.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva provincia.");
        model.addAttribute("province", new Province()); // Crear un nuevo objeto Province
        return "province-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Muestra el formulario para editar una provincia existente.
     *
     * @param id    ID de la provincia a editar.
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para la provincia con ID {}", id);
        Optional<Province> provinceOpt = provinceRepository.findById(id);
        if (!provinceOpt.isPresent()) {
            logger.warn("No se encontró la provincia con ID {}", id);
        }
        model.addAttribute("province", provinceOpt.get());
        return "province-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Crea una nueva provincia en la base de datos.
     *
     * @param province Objeto JSON que representa la nueva provincia.
     * @param locale Idioma de los mensajes de error.
     * @return ResponseEntity con la provincia creada o un mensaje de error.
     */
    @PostMapping
    public ResponseEntity<?> createProvince(@Valid @RequestBody Province province, Locale locale){
        logger.info("Insertando nueva provincia con código {}", province.getCode());
        try {
            // Validar si el código ya existe
            if (provinceRepository.existsProvinceByCode(province.getCode())) {
                String errorMessage = messageSource.getMessage("msg.province-controller.insert.codeExist", null, locale);
                logger.warn("Error al crear provincia: {}", errorMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
            // Guardar la nueva provincia
            Province savedProvince = provinceRepository.save(province);
            logger.info("Provincia creada exitósamente con ID {}", savedProvince.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProvince);
        } catch (Exception e) {
            logger.error("Error al crear la provincia: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la provincia.");
        }
    }

    /**
     * Actualiza una provincia existente por su ID.
     *
     * @param id ID de la provincia a actualizar.
     * @param province Objeto JSON con los nuevos datos.
     * @param locale Idioma de los mensajes de error.
     * @return ResponseEntity con la provincia actualizada o un mensaje de error.
     *
     */

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvince (@PathVariable Long id, @Valid @RequestBody Province province, Locale locale){
        logger.info("Actualizando provincia con ID {}", id);
        try{
            // Verificar si la provincia existe
            Optional<Province> existingProvince = provinceRepository.findById(id);
            if (!existingProvince.isPresent()) {
                logger.warn("No se encontró ninguna provincia con ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La provincia no existe.");
            }
            // Validar si el código ya pertenece a otra provincia
            if(provinceRepository.existsProvinceByCodeAndNotId(province.getCode(),id)){
                String errorMessage = messageSource.getMessage("msg.province-controller.update.codeExist", null, locale);
                logger.warn("Error al actualizar provincia: {}", errorMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
            //Actualizar la provincia
            province.setId(id); // Asegurarse de que el ID no cambie
            Province updateProvince =  provinceRepository.save(province);
            logger.info("Provincia con ID {} actualizada exitósamente.", id);
            return ResponseEntity.ok(updateProvince);
        } catch (Exception e) {
            logger.error("Error al actualizar la provincia con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la provincia.");
        }
    }


    /**
     * Elimina una provincia específica por su ID.
     * @param id ID de la provincia a eliminar.
     * @return ResponseEntity indicando el resultado de la operación.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvince(@PathVariable Long id){
        logger.info("Eliminando provincia con ID {}", id);
        try {
            // Verificar si la provincia existe
            if (!provinceRepository.existsById(id)) {
                logger.warn("No se encontró ninguna provincia con ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La provincia no existe.");
            }
            // Eliminar la provincia
            provinceRepository.deleteById(id);
            logger.info("Provincia con ID {} eliinada exitósamente.", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error al eliminar la provincia con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al elimninar la provincia.");
        }
    }

    private Sort getSort(String sort) {
        if (sort == null) {
            return Sort.by("id").ascending();
        }
        return switch (sort) {
            case "nameAsc" -> Sort.by("name").ascending();
            case "nameDesc" -> Sort.by("name").descending();
            case "codeAsc" -> Sort.by("code").ascending();
            case "codeDesc" -> Sort.by("code").descending();
            case "idDesc" -> Sort.by("id").descending();
            default -> Sort.by("id").ascending();
        };
    }
}

