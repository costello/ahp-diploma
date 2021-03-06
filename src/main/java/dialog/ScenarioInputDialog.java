package dialog;

import model.GlobalTarget;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Sviat on 18.11.14.
 */
public class ScenarioInputDialog extends JDialog {
    final static Logger log = Logger.getLogger(ScenarioInputDialog.class);
    private static MigLayout migLayout = new MigLayout("ins 0, hidemode 3", "", "[][][]");

    private JLabel labelScenarioTitle;
    private JButton buttonSaveScenarioMatrix;
    private JLabel labelCurrentScenario;
    private JPanel panelScenarioInput;
    private JButton buttonScenarioInputDone;
    private JPanel panelRoot;
    private JTextArea textAreaScenario;
    private JButton buttonSaveScenarioList;
    private GlobalTarget target;
    private RTable table;
    private Map<String, Double> bestTargets;
    private ArrayList<String> bestTargetNames;

    public ScenarioInputDialog(StartWindow startWindow, GlobalTarget target) {
        this.target = target;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Введення сценаріїв та їх матриць");
        setContentPane(panelRoot);
        setSize(new Dimension(640, 480));
        setModal(true);
        setLocationRelativeTo(startWindow);
        panelScenarioInput.setLayout(migLayout);

        buttonSaveScenarioList.addActionListener(new SaveScenarioList());
        buttonSaveScenarioMatrix.addActionListener(new SaveScenarioMatrix());
        buttonScenarioInputDone.addActionListener(new InputHasBeenDone());
    }

    private void buildMatrixForScenario() {
        panelScenarioInput.removeAll();
        panelScenarioInput.validate();
        panelScenarioInput.repaint();

        table = new RTable(target.getScenarioListNames());

        panelScenarioInput.add(table, "wrap");

        panelScenarioInput.validate();
        panelScenarioInput.repaint();
    }

    public GlobalTarget display() {
        setVisible(true);
        return target;
    }

    private class SaveScenarioList implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<String> scenarios = new ArrayList<>();

            for (String line : textAreaScenario.getText().split("\n")) {
                scenarios.add(line);
            }

            target.setScenarioList(scenarios);

            bestTargetNames = new ArrayList<>();

            bestTargets = target.getBestTargetsForActors();

            for (String t : bestTargets.keySet()) {
                bestTargetNames.add(t);
            }

            log.debug("best targets size: = " + bestTargetNames.size());

            labelCurrentScenario.setText(String.format("<html>Матриця сценарія для цілі %s</html>", bestTargetNames.get(0)));
            buildMatrixForScenario();
        }
    }

    private class SaveScenarioMatrix implements ActionListener {
        int currentScenarioAndList = 0;

        @Override
        public void actionPerformed(ActionEvent e) {
            target.addScenarioMatrix(table.getMatrix());
            //target.getScenarioList().get(currentScenarioAndList).setMatrix(table.getMatrix());
            currentScenarioAndList++;

            if (currentScenarioAndList >= bestTargets.size()) {
                labelScenarioTitle.setText("Всі сценарії введено");
                buttonSaveScenarioMatrix.setEnabled(false);
            } else {
                labelCurrentScenario.setText(
                        String.format("<html>Матриця сценарія для цілі %s</html>",
                                bestTargetNames.get(currentScenarioAndList)));
                buildMatrixForScenario();
            }
        }
    }

    private class InputHasBeenDone implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
}