package com.ms_books_catalogue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms_books_catalogue.controller.model.LibroDto;
import com.ms_books_catalogue.data.LibroRepository;
import com.ms_books_catalogue.data.model.Libro;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class LibrosServiceImpl2Test {
    @Mock
    private LibroRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LibrosServiceImpl librosService;

    @Test
    void getLibros() {
        // Test 1: Con parámetros de búsqueda - cubre el branch del if
        Libro libro1 = mock(Libro.class);
        List<Libro> librosEncontrados = Arrays.asList(libro1);
        when(repository.search(eq("El Quijote"), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(librosEncontrados);

        List<Libro> result = librosService.getLibros("El Quijote", null, null, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).search("El Quijote", null, null, null, null, null, null);
        verify(repository, never()).getLibros();

        // Test 2: Con parámetro autor
        reset(repository);
        when(repository.search(isNull(), eq("Cervantes"), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(librosEncontrados);

        List<Libro> result2 = librosService.getLibros(null, "Cervantes", null, null, null, null, null);

        assertNotNull(result2);
        verify(repository).search(null, "Cervantes", null, null, null, null, null);

        // Test 3: Con parámetro año
        reset(repository);
        when(repository.search(isNull(), isNull(), isNull(), eq(2023), isNull(), isNull(), isNull()))
                .thenReturn(librosEncontrados);

        List<Libro> result3 = librosService.getLibros(null, null, null, 2023, null, null, null);

        assertNotNull(result3);
        verify(repository).search(null, null, null, 2023, null, null, null);

        // Test 4: Con parámetro visible
        reset(repository);
        when(repository.search(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(true)))
                .thenReturn(librosEncontrados);

        List<Libro> result4 = librosService.getLibros(null, null, null, null, null, null, true);

        assertNotNull(result4);
        verify(repository).search(null, null, null, null, null, null, true);

        // Test 5: Sin parámetros y lista con elementos - cubre el else
        reset(repository);
        when(repository.getLibros()).thenReturn(librosEncontrados);

        List<Libro> result5 = librosService.getLibros(null, null, null, null, null, null, null);

        assertNotNull(result5);
        assertEquals(1, result5.size());
        verify(repository).getLibros();

        // Test 6: Sin parámetros y lista vacía - cubre el return null
        reset(repository);
        when(repository.getLibros()).thenReturn(Collections.emptyList());

        List<Libro> result6 = librosService.getLibros(null, null, null, null, null, null, null);

        assertNull(result6);
        verify(repository).getLibros();
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
        // Test 1: Libro existe - cubre el if(libro != null)
        Libro libro = mock(Libro.class);
        when(repository.getById(1L)).thenReturn(libro);

        Boolean result = librosService.eliminarLibro("1");

        assertTrue(result);
        verify(repository).getById(1L);
        verify(repository).delete(libro);

        // Test 2: Libro no existe - cubre el else
        when(repository.getById(999L)).thenReturn(null);

        Boolean result2 = librosService.eliminarLibro("999");

        assertFalse(result2);
        verify(repository).getById(999L);
    }

    @Test
    void crearLibro() {
        // Test 1: Datos válidos
        LibroDto libroDto = mock(LibroDto.class);
        when(libroDto.getTitulo()).thenReturn("El Quijote");
        when(libroDto.getAutor()).thenReturn("Miguel de Cervantes");
        when(libroDto.getEditorial()).thenReturn("Planeta");
        when(libroDto.getGenero()).thenReturn("Novela");
        when(libroDto.getPrecio()).thenReturn(25.99f);
        when(libroDto.getStock()).thenReturn(10);
        when(libroDto.getVisible()).thenReturn(true);

        Libro libroCreado = mock(Libro.class);
        when(repository.save(any(Libro.class))).thenReturn(libroCreado);

        Libro result = librosService.crearLibro(libroDto);

        assertNotNull(result);
        verify(repository).save(any(Libro.class));

        // Test 2: DTO nulo
        Libro result2 = librosService.crearLibro(null);
        assertNull(result2);

        // Test 3: Título vacío
        LibroDto dtoTituloVacio = mock(LibroDto.class);
        when(dtoTituloVacio.getTitulo()).thenReturn("");
        Libro result3 = librosService.crearLibro(dtoTituloVacio);
        assertNull(result3);

        // Test 4: Autor vacío
        LibroDto dtoAutorVacio = mock(LibroDto.class);
        when(dtoAutorVacio.getTitulo()).thenReturn("Titulo");
        when(dtoAutorVacio.getAutor()).thenReturn("");
        Libro result4 = librosService.crearLibro(dtoAutorVacio);
        assertNull(result4);

        // Test 5: Precio nulo
        LibroDto dtoPrecioNulo = mock(LibroDto.class);
        when(dtoPrecioNulo.getTitulo()).thenReturn("Titulo");
        when(dtoPrecioNulo.getAutor()).thenReturn("Autor");
        when(dtoPrecioNulo.getEditorial()).thenReturn("Editorial");
        when(dtoPrecioNulo.getGenero()).thenReturn("Genero");
        when(dtoPrecioNulo.getPrecio()).thenReturn(null);
        Libro result5 = librosService.crearLibro(dtoPrecioNulo);
        assertNull(result5);

        // Test 6: Stock nulo
        LibroDto dtoStockNulo = mock(LibroDto.class);
        when(dtoStockNulo.getTitulo()).thenReturn("Titulo");
        when(dtoStockNulo.getAutor()).thenReturn("Autor");
        when(dtoStockNulo.getEditorial()).thenReturn("Editorial");
        when(dtoStockNulo.getGenero()).thenReturn("Genero");
        when(dtoStockNulo.getPrecio()).thenReturn(25.99f);
        when(dtoStockNulo.getStock()).thenReturn(null);
        Libro result6 = librosService.crearLibro(dtoStockNulo);
        assertNull(result6);

        // Test 7: Visible nulo
        LibroDto dtoVisibleNulo = mock(LibroDto.class);
        when(dtoVisibleNulo.getTitulo()).thenReturn("Titulo");
        when(dtoVisibleNulo.getAutor()).thenReturn("Autor");
        when(dtoVisibleNulo.getEditorial()).thenReturn("Editorial");
        when(dtoVisibleNulo.getGenero()).thenReturn("Genero");
        when(dtoVisibleNulo.getPrecio()).thenReturn(25.99f);
        when(dtoVisibleNulo.getStock()).thenReturn(10);
        when(dtoVisibleNulo.getVisible()).thenReturn(null);
        Libro result7 = librosService.crearLibro(dtoVisibleNulo);
        assertNull(result7);
    }

    @Test
    void actualizarLibro() {
        // Test único y simple: libro no existe
        when(repository.getById(999L)).thenReturn(null);

        Libro result = librosService.actualizarLibro("999", "{}");

        assertNull(result);
        verify(repository).getById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    void ActualizarLibro() {
        // Test 1: Libro existe - versión simple que funciona
        Libro libro = mock(Libro.class);
        LibroDto libroDto = mock(LibroDto.class);

        when(repository.getById(1L)).thenReturn(libro);
        when(repository.save(libro)).thenReturn(libro);

        Libro result = librosService.actualizarLibro("1", libroDto);

        assertNotNull(result);
        assertEquals(libro, result);
        verify(repository).getById(1L);
        verify(libro).update(libroDto);
        verify(repository).save(libro);

        // Test 2: Libro no existe
        when(repository.getById(999L)).thenReturn(null);

        Libro result2 = librosService.actualizarLibro("999", libroDto);

        assertNull(result2);
        verify(repository).getById(999L);
    }

}