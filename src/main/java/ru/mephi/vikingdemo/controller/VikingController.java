package ru.mephi.vikingdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import java.util.List;

@RestController
@RequestMapping("/api/vikings")
@Tag(name = "Vikings", description = "Операции с викингами")
public class VikingController {

    private final VikingService vikingService;
    private final VikingListener vikingListener;

    public VikingController(VikingService vikingService, VikingListener vikingListener) {
        this.vikingService = vikingService;
        this.vikingListener = vikingListener;
    }

    @GetMapping
    @Operation(summary = "Получить список всех викингов", operationId = "getAllVikings")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список успешно получен")})
    public List<Viking> getAllVikings() {
        System.out.println("GET /api/vikings called");
        return vikingService.findAll();
    }

    @GetMapping("/test")
    @Operation(summary = "Получить список тестовых викингов", operationId = "getTest")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список успешно получен")})
    public List<String> test() {
        return List.of("Ragnar", "Bjorn");
    }

    @PostMapping("/post")
    @Operation(summary = "Создать викинга со случайными параметрами", operationId = "post")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Викинг успешно создан")})
    public void addRandomViking() {
        System.out.println("POST api/vikings/post called");
        vikingListener.testAdd();
    }

    // добавление конкретного викинга
    @PostMapping
    @Operation(summary = "Добавить конкретного викинга", operationId = "addViking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Викинг успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ResponseEntity<Viking> addViking(@RequestBody Viking viking) {
        System.out.println("POST /api/vikings called");
        Viking saved = vikingService.addViking(viking);
        vikingListener.onVikingAdded(saved);
        return ResponseEntity.ok(saved);
    }

    // удаление викинга по id
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить викинга по id", operationId = "deleteViking")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Викинг удалён"),
            @ApiResponse(responseCode = "404", description = "Викинг не найден")
    })
    public ResponseEntity<Void> deleteViking(@PathVariable int id) {
        System.out.println("DELETE /api/vikings/" + id + " called");
        vikingService.deleteById(id);
        vikingListener.onVikingDeleted(id);
        return ResponseEntity.noContent().build();
    }

    // перезапись параметров викинга
    @PutMapping("/{id}")
    @Operation(summary = "Перезаписать параметры викинга", operationId = "updateViking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Викинг обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ResponseEntity<Viking> updateViking(@PathVariable int id, @RequestBody Viking viking) {
        System.out.println("PUT /api/vikings/" + id + " called");
        Viking updated = vikingService.updateViking(id, viking);
        vikingListener.onVikingUpdated(updated);
        return ResponseEntity.ok(updated);
    }
}