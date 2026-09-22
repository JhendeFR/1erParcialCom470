package com.ms_books_catalogue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms_books_catalogue.controller.model.LibroDto;
import com.ms_books_catalogue.data.LibroRepository;
import com.ms_books_catalogue.data.model.Libro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibrosServiceImplTest {

    @Mock
    private LibroRepository repository;

    private LibrosServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new LibrosServiceImpl(repository, new ObjectMapper());
    }

    @Test
    void getLibrosUsesSearchWhenAnyFilterIsPresent() {
        List<Libro> expected = List.of(libro(1L));
        when(repository.search("1984", null, null, null, null, null, null)).thenReturn(expected);

        List<Libro> result = service.getLibros("1984", null, null, null, null, null, null);

        assertSame(expected, result);
        verify(repository).search("1984", null, null, null, null, null, null);
        verify(repository, never()).getLibros();
    }

    @Test
    void getLibrosUsesSearchForEachSupportedFilter() {
        List<Libro> expected = List.of(libro(1L));
        when(repository.search(null, "George", null, null, null, null, null)).thenReturn(expected);
        when(repository.search(null, null, "Roca", null, null, null, null)).thenReturn(expected);
        when(repository.search(null, null, null, 1984, null, null, null)).thenReturn(expected);
        when(repository.search(null, null, null, null, "dystopian", null, null)).thenReturn(expected);
        when(repository.search(null, null, null, null, null, "Juvenil", null)).thenReturn(expected);
        when(repository.search(null, null, null, null, null, null, true)).thenReturn(expected);

        assertSame(expected, service.getLibros(null, "George", null, null, null, null, null));
        assertSame(expected, service.getLibros(null, null, "Roca", null, null, null, null));
        assertSame(expected, service.getLibros(null, null, null, 1984, null, null, null));
        assertSame(expected, service.getLibros(null, null, null, null, "dystopian", null, null));
        assertSame(expected, service.getLibros(null, null, null, null, null, "Juvenil", null));
        assertSame(expected, service.getLibros(null, null, null, null, null, null, true));

        verify(repository).search(null, "George", null, null, null, null, null);
        verify(repository).search(null, null, "Roca", null, null, null, null);
        verify(repository).search(null, null, null, 1984, null, null, null);
        verify(repository).search(null, null, null, null, "dystopian", null, null);
        verify(repository).search(null, null, null, null, null, "Juvenil", null);
        verify(repository).search(null, null, null, null, null, null, true);
    }

    @Test
    void getLibrosUsesRepositoryWhenNoFiltersArePresent() {
        List<Libro> expected = List.of(libro(1L));
        when(repository.getLibros()).thenReturn(expected);

        assertSame(expected, service.getLibros(null, null, null, null, null, null, null));
        verify(repository).getLibros();
    }

    @Test
    void getLibrosReturnsNullWhenRepositoryIsEmpty() {
        when(repository.getLibros()).thenReturn(Collections.emptyList());

        assertNull(service.getLibros(null, null, null, null, null, null, null));
    }

    @Test
    void getLibroReturnsExistingBook() {
        Libro expected = libro(1L);
        when(repository.getById(1L)).thenReturn(expected);

        assertSame(expected, service.getLibro("1"));
        verify(repository).getById(1L);
    }

    @Test
    void getLibroReturnsNullWhenBookDoesNotExist() {
        when(repository.getById(99L)).thenReturn(null);

        assertNull(service.getLibro("99"));
    }

    @Test
    void getLibroRejectsNonNumericId() {
        assertThrows(NumberFormatException.class, () -> service.getLibro("abc"));
        verify(repository, never()).getById(any(Long.class));
    }

    @Test
    void eliminarLibroDeletesExistingBook() {
        Libro book = libro(1L);
        when(repository.getById(1L)).thenReturn(book);

        assertTrue(service.eliminarLibro("1"));
        verify(repository).delete(book);
    }

    @Test
    void eliminarLibroReturnsFalseWhenBookDoesNotExist() {
        when(repository.getById(99L)).thenReturn(null);

        assertFalse(service.eliminarLibro("99"));
        verify(repository, never()).delete(any(Libro.class));
    }

    @Test
    void crearLibroSavesValidDto() {
        when(repository.save(any(Libro.class))).thenReturn(libro(1L));

        Libro result = service.crearLibro(validDto());

        assertEquals(1L, result.getId());
        verify(repository).save(any(Libro.class));
    }

    @Test
    void crearLibroRejectsNullDto() {
        assertNull(service.crearLibro(null));
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    void crearLibroRejectsMissingRequiredText() {
        LibroDto dto = validDto();
        dto.setTitulo(null);

        assertNull(service.crearLibro(dto));
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    void crearLibroRejectsNullAuthor() {
        LibroDto dto = validDto();
        dto.setAutor(null);

        assertNull(service.crearLibro(dto));
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    void crearLibroRejectsNullEditorial() {
        LibroDto dto = validDto();
        dto.setEditorial(null);

        assertNull(service.crearLibro(dto));
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    void crearLibroRejectsBlankRequiredText() {
        LibroDto dto = validDto();
        dto.setGenero("   ");

        assertNull(service.crearLibro(dto));
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    void crearLibroRejectsMissingNumericOrBooleanValue() {
        LibroDto dto = validDto();
        dto.setPrecio(null);

        assertNull(service.crearLibro(dto));
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    void crearLibroRejectsNullStock() {
        LibroDto dto = validDto();
        dto.setStock(null);

        assertNull(service.crearLibro(dto));
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    void crearLibroRejectsNullVisibility() {
        LibroDto dto = validDto();
        dto.setVisible(null);

        assertNull(service.crearLibro(dto));
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    void actualizarLibroWithDtoUpdatesAndSavesExistingBook() {
        Libro book = libro(1L);
        when(repository.getById(1L)).thenReturn(book);
        when(repository.save(book)).thenReturn(book);

        Libro result = service.actualizarLibro("1", validDto());

        assertSame(book, result);
        assertEquals("1984", book.getTitulo());
        verify(repository).save(book);
    }

    @Test
    void actualizarLibroWithDtoReturnsNullWhenBookDoesNotExist() {
        when(repository.getById(99L)).thenReturn(null);

        assertNull(service.actualizarLibro("99", validDto()));
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    void actualizarLibroWithPatchAppliesPatchAndSavesBook() {
        Libro original = libro(1L);
        when(repository.getById(1L)).thenReturn(original);
        when(repository.save(any(Libro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Libro result = service.actualizarLibro("1", "{\"titulo\":\"Nuevo titulo\"}");

        assertEquals("Nuevo titulo", result.getTitulo());
        verify(repository).save(any(Libro.class));
    }

    @Test
    void actualizarLibroWithPatchReturnsNullForInvalidJson() {
        when(repository.getById(1L)).thenReturn(libro(1L));

        assertNull(service.actualizarLibro("1", "{json invalido"));
        verify(repository, never()).save(any(Libro.class));
    }

    @Test
    void actualizarLibroWithPatchReturnsNullWhenBookDoesNotExist() {
        when(repository.getById(99L)).thenReturn(null);

        assertNull(service.actualizarLibro("99", "{}"));
        verify(repository, never()).save(any(Libro.class));
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

    private LibroDto validDto() {
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
