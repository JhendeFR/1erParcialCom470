package com.ms_books_catalogue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms_books_catalogue.data.LibroRepository;
import com.ms_books_catalogue.data.model.Libro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class LibrosServiceImplTest {
    @Mock
    private LibroRepository repository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    LibrosService librosService;

    @InjectMocks
    private LibrosServiceImpl service;



    @BeforeEach
    void setUp() {
    MockitoAnnotations.openMocks(this);

    }

    @Test
    void getLibros() {
        // Test 1: Con parámetros de búsqueda - cubre el branch del if
        Libro libro1 = mock(Libro.class);
        List<Libro> librosEncontrados = Arrays.asList(libro1);
        when(repository.search(eq("El Quijote"), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(librosEncontrados);


        List<Libro> result = librosService.getLibros("El Quijote", null, null, null, null, null, null);

        assertNotNull(result);
        //assertEquals(1, result.size());
        verify(repository).search("El Quijote", null, null, null, null, null, null);
        verify(repository, never()).getLibros();


    }

    @Test
    void getLibro() {
        // Test 1: Libro existe
        Libro libroExistente = mock(Libro.class);
        when(repository.getById(1L)).thenReturn(libroExistente);

        Libro result = librosService.getLibro("1");

        assertEquals(libroExistente, result); // Cambiado de assertNotNull
        verify(repository).getById(1L);

        // Test 2: Libro no existe
        when(repository.getById(999L)).thenReturn(null);

        Libro result2 = librosService.getLibro("999");

        assertNull(result2);
        verify(repository).getById(999L);
    }

    @Test
    void eliminarLibro() {
    }

    @Test
    void crearLibro() {
    }

    @Test
    void actualizarLibro() {
    }

    @Test
    void testActualizarLibro() {
    }
}