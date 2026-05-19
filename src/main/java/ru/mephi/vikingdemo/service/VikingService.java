package ru.mephi.vikingdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.repository.VikingStorage;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class VikingService {

    private final VikingFactory vikingFactory;
    private final VikingStorage vikingStorage;

    @Autowired
    public VikingService(VikingFactory vikingFactory, VikingStorage vikingStorage) {
        this.vikingFactory = vikingFactory;
        this.vikingStorage = vikingStorage;
    }

    public List<Viking> findAll() {
        return vikingStorage.findAll();
    }

    public Viking createRandomViking() {
        Viking viking = vikingFactory.createRandomViking();
        return vikingStorage.save(viking);
    }

    public Viking addViking(Viking viking) {
        return vikingStorage.save(viking);
    }

    public Viking updateViking(int id, Viking viking) {
        return vikingStorage.update(id, viking);
    }

    public void deleteById(int id) {
        vikingStorage.deleteById(id);
    }
    
    public List<Viking> bulkGenerateVikings(int count) {
        Supplier<Viking> vikingSupplier = () -> vikingFactory.createRandomViking();

        return IntStream.range(0, count)
                .mapToObj(i -> vikingSupplier.get())
                .map(vikingStorage::save)
                .collect(Collectors.toList());
    }
}
