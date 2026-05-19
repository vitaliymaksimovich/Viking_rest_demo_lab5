package ru.mephi.vikingdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mephi.vikingdemo.gui.VikingDesktopFrame;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.SwingUtilities;
import java.util.List;

@Component
public class VikingListener {

    private final VikingService service;
    private VikingDesktopFrame gui;

    @Autowired
    public VikingListener(VikingService service) {
        this.service = service;
    }

    public void setGui(VikingDesktopFrame gui) {
        this.gui = gui;
    }

    void testAdd() {
        Viking created = service.createRandomViking();
        if (gui != null) {
            gui.addNewViking(created);
        }
    }

    public void onVikingAdded(Viking viking) {
        if (gui != null) {
            SwingUtilities.invokeLater(() -> gui.addNewViking(viking));
        }
    }

    public void onVikingDeleted(int id) {
        if (gui != null) {
            SwingUtilities.invokeLater(() -> gui.removeViking(id));
        }
    }

    public void onVikingUpdated(Viking viking) {
        if (gui != null) {
            SwingUtilities.invokeLater(() -> gui.updateViking(viking));
        }
    }

    public void onVikingsBulkAdded(List<Viking> vikings) {
        if (gui != null) {
            SwingUtilities.invokeLater(() -> vikings.forEach(gui::addNewViking));
        }
    }
}
