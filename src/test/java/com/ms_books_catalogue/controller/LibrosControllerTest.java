package com.ms_books_catalogue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms_books_catalogue.controller.model.LibroDto;
import com.ms_books_catalogue.data.model.Libro;
import com.ms_books_catalogue.service.LibrosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibrosController.class)
class LibrosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LibrosService service;

    @Test
    void getLibrosReturnsBooks() throws Exception {
        Libro libro = libro(1L);
        when(service.getLibros(null, null, null, null, null, null, null))
                .thenReturn(List.of(libro));

        mockMvc.perform(get("/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("1984"));

        verify(service).getLibros(null, null, null, null, null, null, null);
    }

    @Test
    void getLibrosReturnsEmptyListWhenServiceReturnsNull() throws Exception {
        when(service.getLibros(null, null, null, null, null, null, null))
                .thenReturn(null);

        mockMvc.perform(get("/libros"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getLibrosPassesQueryParametersToService() throws Exception {
        when(service.getLibros("1984", "George", null, 1984, null, "Juvenil", true))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/libros")
                        .param("titulo", "1984")
                        .param("autor", "George")
                        .param("anio", "1984")
                        .param("genero", "Juvenil")
                        .param("visible", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service).getLibros("1984", "George", null, 1984, null, "Juvenil", true);
    }

    @Test
    void getLibroReturnsBook() throws Exception {
        when(service.getLibro("1")).thenReturn(libro(1L));

        mockMvc.perform(get("/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("1984"));
    }

    @Test
    void getLibroReturnsNotFoundWhenBookDoesNotExist() throws Exception {
        when(service.getLibro("99")).thenReturn(null);

        mockMvc.perform(get("/libros/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addLibroReturnsCreated() throws Exception {
        LibroDto dto = libroDto();
        when(service.crearLibro(any(LibroDto.class))).thenReturn(libro(1L));

        mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(service).crearLibro(any(LibroDto.class));
    }

    @Test
    void addLibroReturnsBadRequestWhenServiceRejectsBook() throws Exception {
        when(service.crearLibro(any(LibroDto.class))).thenReturn(null);

        mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateLibroReturnsUpdatedBook() throws Exception {
        LibroDto dto = libroDto();
        when(service.actualizarLibro(eq("1"), any(LibroDto.class))).thenReturn(libro(1L));

        mockMvc.perform(put("/libros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateLibroReturnsNotFoundWhenBookDoesNotExist() throws Exception {
        when(service.actualizarLibro(eq("99"), any(LibroDto.class))).thenReturn(null);

        mockMvc.perform(put("/libros/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchLibroReturnsUpdatedBook() throws Exception {
        when(service.actualizarLibro("1", "{\"titulo\":\"Nuevo título\"}"))
                .thenReturn(libro(1L));

        mockMvc.perform(patch("/libros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Nuevo título\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void patchLibroReturnsNotFoundWhenBookDoesNotExist() throws Exception {
        when(service.actualizarLibro("99", "{}"))
                .thenReturn(null);

        mockMvc.perform(patch("/libros/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteLibroReturnsOkWhenBookIsDeleted() throws Exception {
        when(service.eliminarLibro("1")).thenReturn(true);

        mockMvc.perform(delete("/libros/1"))
                .andExpect(status().isOk());

        verify(service).eliminarLibro("1");
    }

    @Test
    void deleteLibroReturnsNotFoundWhenBookDoesNotExist() throws Exception {
        when(service.eliminarLibro("99")).thenReturn(false);

        mockMvc.perform(delete("/libros/99"))
                .andExpect(status().isNotFound());
    }

    private Libro libro(Long id) {
        return Libro.builder()
                .id(id)
                .titulo("1984")
                .autor("George Orwell")
                .editorial("Roca Editorial")
                .anio(1984)
                .genero("Juvenil")
                .precio(16.50f)
                .stock(10)
                .visible(true)
                .build();
    }

    private LibroDto libroDto() {
        LibroDto dto = new LibroDto();
        dto.setTitulo("1984");
        dto.setAutor("George Orwell");
        dto.setEditorial("Roca Editorial");
        dto.setAnio(1984);
        dto.setGenero("Juvenil");
        dto.setPrecio(16.50f);
        dto.setStock(10);
        dto.setVisible(true);
        return dto;
    }
}
