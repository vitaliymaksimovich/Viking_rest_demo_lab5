package ru.mephi.vikingdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class VikingLambdaService {

    private final VikingService vikingService;

    @Autowired
    public VikingLambdaService(VikingService vikingService) {
        this.vikingService = vikingService;
    }


    public long countByAgeGreaterThan(int age) {
        Predicate<Viking> filter = v -> v.age() > age;
        return vikingService.findAll().stream().filter(filter).count();
    }

    public long countByAgeLessThan(int age) {
        Predicate<Viking> filter = v -> v.age() < age;
        return vikingService.findAll().stream().filter(filter).count();
    }


    public long countByAgeInRange(int minAge, int maxAge) {
        Predicate<Viking> filter = v -> v.age() >= minAge && v.age() <= maxAge;
        return vikingService.findAll().stream().filter(filter).count();
    }


    public long countByAgeOutOfRange(int minAge, int maxAge) {
        Predicate<Viking> filter = v -> v.age() < minAge || v.age() > maxAge;
        return vikingService.findAll().stream().filter(filter).count();
    }


    public long countByBeardStyleAndHairColor(BeardStyle beardStyle, HairColor hairColor) {
        Predicate<Viking> filter = v -> v.beardStyle() == beardStyle && v.hairColor() == hairColor;
        return vikingService.findAll().stream().filter(filter).count();
    }


    public long countWithOneOrTwoAxes() {
        // Лямбда: подсчитать количество топоров у викинга
        java.util.function.ToLongFunction<Viking> axeCounter = v ->
                v.equipment().stream()
                        .filter(e -> e.name().toLowerCase().contains("axe"))
                        .count();

        // Предикат: настоящий викинг (есть борода) и имеет 1 или 2 топора
        Predicate<Viking> filter = v -> {
            boolean hasBeard = v.beardStyle() != BeardStyle.CLEAN_SHAVEN;
            long axes = axeCounter.applyAsLong(v);
            return hasBeard && (axes == 1 || axes == 2);
        };

        return vikingService.findAll().stream().filter(filter).count();
    }

    public Optional<Viking> getRandomVikingTallerThan180() {
        Predicate<Viking> tallFilter = v -> v.heightCm() > 180;

        List<Viking> tallVikings = vikingService.findAll().stream()
                .filter(tallFilter)
                .collect(Collectors.toList());

        if (tallVikings.isEmpty()) {
            return Optional.empty();
        }

        // Лямбда-выбор случайного элемента
        java.util.function.Supplier<Viking> randomPicker =
                () -> tallVikings.get(new Random().nextInt(tallVikings.size()));

        return Optional.of(randomPicker.get());
    }


    public List<Viking> getVikingsWithLegendaryEquipment() {
        Predicate<Viking> hasLegendary = v ->
                v.equipment().stream()
                        .anyMatch(e -> "Legendary".equalsIgnoreCase(e.quality()));

        return vikingService.findAll().stream()
                .filter(hasLegendary)
                .collect(Collectors.toList());
    }


    public List<Viking> getRedBeardedVikingsSortedByAge() {
        Predicate<Viking> isRedBearded = v ->
                v.hairColor() == HairColor.Red && v.beardStyle() != BeardStyle.CLEAN_SHAVEN;

        Comparator<Viking> byAge = Comparator.comparingInt(Viking::age);

        return vikingService.findAll().stream()
                .filter(isRedBearded)
                .sorted(byAge)
                .collect(Collectors.toList());
    }


    public OptionalInt findMaxId() {
        // Собираем ID в явный массив Integer[]
        Integer[] ids = vikingService.findAll().stream()
                .map(Viking::id)
                .toArray(Integer[]::new);

        // Через лямбду ищем максимум
        return Arrays.stream(ids)
                .mapToInt(Integer::intValue)
                .max();
    }


    public List<Viking> getVikingsWithEvenIds() {
        List<Viking> allVikings = vikingService.findAll();

        // Собираем все ID в явный массив Integer[]
        Integer[] ids = allVikings.stream()
                .map(Viking::id)
                .toArray(Integer[]::new);

        // Через лямбду фильтруем чётные ID
        Predicate<Integer> isEven = id -> id % 2 == 0;

        Set<Integer> evenIdSet = Arrays.stream(ids)
                .filter(isEven::test)
                .collect(Collectors.toSet());

        // Ищем викингов по отобранным ID
        return allVikings.stream()
                .filter(v -> evenIdSet.contains(v.id()))
                .collect(Collectors.toList());
    }


    public Integer[] getEvenIds() {
        Integer[] ids = vikingService.findAll().stream()
                .map(Viking::id)
                .toArray(Integer[]::new);

        Predicate<Integer> isEven = id -> id % 2 == 0;

        return Arrays.stream(ids)
                .filter(isEven::test)
                .toArray(Integer[]::new);
    }
}
