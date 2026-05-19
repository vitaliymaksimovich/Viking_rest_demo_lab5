package ru.mephi.vikingdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingLambdaService;
import ru.mephi.vikingdemo.service.VikingService;
// VikingListener в том же пакете — импорт не нужен

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

@RestController
@RequestMapping("/api/vikings/lambda")
@Tag(name = "Vikings Lambda", description = "Аналитика викингов через лямбда-функции (Лаба 5)")
public class VikingLambdaController {

    private final VikingLambdaService lambdaService;
    private final VikingService vikingService;
    private final VikingListener vikingListener;

    public VikingLambdaController(VikingLambdaService lambdaService,
                                  VikingService vikingService,
                                  VikingListener vikingListener) {
        this.lambdaService = lambdaService;
        this.vikingService = vikingService;
        this.vikingListener = vikingListener;
    }


    @GetMapping("/count/age-greater")
    @Operation(summary = "Количество викингов старше заданного возраста")
    public Map<String, Long> countAgeGreater(@RequestParam int age) {
        return Map.of("count", lambdaService.countByAgeGreaterThan(age));
    }

    @GetMapping("/count/age-less")
    @Operation(summary = "Количество викингов младше заданного возраста")
    public Map<String, Long> countAgeLess(@RequestParam int age) {
        return Map.of("count", lambdaService.countByAgeLessThan(age));
    }

    @GetMapping("/count/age-in-range")
    @Operation(summary = "Количество викингов в диапазоне возраста [min, max]")
    public Map<String, Long> countAgeInRange(@RequestParam int min, @RequestParam int max) {
        return Map.of("count", lambdaService.countByAgeInRange(min, max));
    }

    @GetMapping("/count/age-out-of-range")
    @Operation(summary = "Количество викингов вне диапазона возраста")
    public Map<String, Long> countAgeOutOfRange(@RequestParam int min, @RequestParam int max) {
        return Map.of("count", lambdaService.countByAgeOutOfRange(min, max));
    }

    @GetMapping("/count/beard-and-hair")
    @Operation(summary = "Количество викингов по форме бороды И цвету волос")
    public Map<String, Long> countByBeardAndHair(
            @RequestParam BeardStyle beardStyle,
            @RequestParam HairColor hairColor) {
        return Map.of("count", lambdaService.countByBeardStyleAndHairColor(beardStyle, hairColor));
    }

    @GetMapping("/count/with-axes")
    @Operation(summary = "Количество бородатых викингов с 1 или 2 топорами")
    public Map<String, Long> countWithAxes() {
        return Map.of("count", lambdaService.countWithOneOrTwoAxes());
    }


    @GetMapping("/random-tall")
    @Operation(summary = "Случайный викинг ростом выше 180 см")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Викинг найден"),
            @ApiResponse(responseCode = "404", description = "Нет викингов выше 180 см")
    })
    public ResponseEntity<Viking> getRandomTallViking() {
        Optional<Viking> viking = lambdaService.getRandomVikingTallerThan180();
        return viking.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/legendary")
    @Operation(summary = "Все викинги с легендарным снаряжением")
    public List<Viking> getLegendaryVikings() {
        return lambdaService.getVikingsWithLegendaryEquipment();
    }

    @GetMapping("/red-bearded-sorted")
    @Operation(summary = "Рыжебородые викинги, отсортированные по возрасту")
    public List<Viking> getRedBeardedSorted() {
        return lambdaService.getRedBeardedVikingsSortedByAge();
    }


    @GetMapping("/max-id")
    @Operation(summary = "Последняя запись (максимальный ID)")
    public ResponseEntity<Map<String, Integer>> getMaxId() {
        OptionalInt maxId = lambdaService.findMaxId();
        if (maxId.isPresent()) {
            return ResponseEntity.ok(Map.of("maxId", maxId.getAsInt()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/even-ids")
    @Operation(summary = "Все чётные ID и соответствующие им викинги")
    public Map<String, Object> getEvenIds() {
        Integer[] evenIds = lambdaService.getEvenIds();
        List<Viking> vikings = lambdaService.getVikingsWithEvenIds();
        return Map.of(
                "evenIds", evenIds,
                "vikings", vikings
        );
    }


    @PostMapping("/bulk-generate")
    @Operation(summary = "Массовая генерация викингов (через фабрику + лямбды)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Викинги успешно созданы")})
    public List<Viking> bulkGenerate(
            @RequestParam @Parameter(description = "Количество викингов", example = "10") int count) {
        List<Viking> created = vikingService.bulkGenerateVikings(count);
        vikingListener.onVikingsBulkAdded(created);
        return created;
    }
}
