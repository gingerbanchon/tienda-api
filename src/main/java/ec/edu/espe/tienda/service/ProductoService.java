package ec.edu.espe.tienda.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.espe.tienda.exception.ProductoInvalidoException;
import ec.edu.espe.tienda.exception.ProductoNoEncontradoException;
import ec.edu.espe.tienda.model.Producto;

@Service
public class ProductoService {

    private final List<Producto> productos = new ArrayList<>(List.of(
            new Producto(1L, "Laptop", 850.00, 10),
            new Producto(2L, "Mouse", 18.50, 40),
            new Producto(3L, "Teclado", 35.00, 25)
    ));

    public synchronized List<Producto> listar() {
        return List.copyOf(productos);
    }

    public synchronized Producto buscarPorId(Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    public synchronized Producto agregar(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new ProductoInvalidoException("El nombre es obligatorio");
        }
        if (!Double.isFinite(producto.getPrecio()) || producto.getPrecio() <= 0) {
            throw new ProductoInvalidoException("El precio debe ser mayor a cero");
        }
        if (producto.getStock() < 0) {
            throw new ProductoInvalidoException("El stock no puede ser negativo");
        }
        if (producto.getId() == null) {
            long siguiente = productos.stream()
                    .mapToLong(Producto::getId)
                    .max()
                    .orElse(0) + 1;
            producto.setId(siguiente);
        } else if (producto.getId() <= 0) {
            throw new ProductoInvalidoException("El id debe ser positivo");
        } else if (productos.stream().anyMatch(p -> p.getId().equals(producto.getId()))) {
            throw new ProductoInvalidoException("Ya existe un producto con ese id");
        }
        productos.add(producto);
        return producto;
    }

    public synchronized void eliminar(Long id) {
        boolean eliminado = productos.removeIf(p -> p.getId().equals(id));
        if (!eliminado) {
            throw new ProductoNoEncontradoException(id);
        }
    }
}