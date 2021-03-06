package dialog;

import dialog.secondAlgo.*;
import model.DConfig;
import model.GlobalTarget;
import model.SGlobalTarget;
import util.Calculate;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Sviat on 18.11.14.
 */
public class StartWindow extends JFrame {
    final static Logger log = Logger.getLogger(StartWindow.class);
    private static MigLayout migLayout = new MigLayout("ins 0, hidemode 3", "", "[][][]");
    private SGlobalTarget starget;

    private JTextField textFieldTarget;
    private JButton buttonAddActors;
    private JButton buttonActorTargetMatrixInputDone;
    private JPanel panelRoot;
    private JPanel panelMatrix;
    private JButton buttonStartScenarioInput;
    private JButton buttonRunSecond;
    private JButton buttonSaveActorMatrix;
    private GlobalTarget target;
    private RTable actorTargetMatrix;

    public static void main(String[] args) {
        log.debug("ok");
        StartWindow start = new StartWindow();
        start.setTitle("Магістерська робота Горошка Є.М., ІСМм-21");

        start.setSize(800, 400);
        start.setContentPane(start.panelRoot);
        start.setVisible(true);

        start.setLocationRelativeTo(null);

        start.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public StartWindow() throws HeadlessException {
        buttonAddActors.addActionListener(new OpenActorInputDialog(this));
        buttonActorTargetMatrixInputDone.addActionListener(new OpenActorsTargetsInput(this));
        buttonStartScenarioInput.addActionListener(new StartScenarioInput(this));

        buttonRunSecond.addActionListener(new RunSecond(this));

        buttonSaveActorMatrix.addActionListener(new OnMatrixSaved(this));

        reInitButtonsForAlog();
    }

    public void reInitButtonsForAlog() {
        panelMatrix.removeAll();
        panelMatrix.validate();
        panelMatrix.repaint();

        if (!DConfig.isSecondAlgo) {
            buttonRunSecond.setVisible(false);
            buttonSaveActorMatrix.setVisible(false);
        }
        else {
            buttonRunSecond.setVisible(true);
            buttonSaveActorMatrix.setVisible(true);
            buttonAddActors.setVisible(false);
            buttonStartScenarioInput.setVisible(false);
            buttonActorTargetMatrixInputDone.setVisible(false);
        }
    }

    private class OpenActorInputDialog implements ActionListener {
        private StartWindow startWindow;

        public OpenActorInputDialog(StartWindow startWindow) {

            this.startWindow = startWindow;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            target = new GlobalTarget(textFieldTarget.getText());
            ActorsInputDialog aDialog = new ActorsInputDialog(startWindow, target);
            target = aDialog.display();

            buttonAddActors.setEnabled(false);
            addMatrix();
        }
    }

    private void addMatrix() {
        if (!DConfig.isSecondAlgo) {
            actorTargetMatrix = new RTable(target.getActorsNames());
        }
        else {
            actorTargetMatrix = new RTable(starget.getActors());
        }

        panelMatrix.removeAll();
        panelMatrix.validate();
        panelMatrix.repaint();

        panelMatrix.setLayout(migLayout);
        panelMatrix.add(actorTargetMatrix, "w 100%, h 100%");

        panelMatrix.validate();
        panelMatrix.repaint();
    }

    private class OpenActorsTargetsInput implements ActionListener {
        private StartWindow startWindow;

        public OpenActorsTargetsInput(StartWindow startWindow) {
            this.startWindow = startWindow;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            target.setMatrix(actorTargetMatrix.getMatrix());
            ActorsTargetsInput aDialog = new ActorsTargetsInput(startWindow, target);
            target = aDialog.display();
        }
    }

    private class StartScenarioInput implements ActionListener {
        private StartWindow startWindow;

        public StartScenarioInput(StartWindow startWindow) {

            this.startWindow = startWindow;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ScenarioInputDialog sDialog = new ScenarioInputDialog(startWindow, target);
            target = sDialog.display();

            log.debug("scenario input has been done");

            ArrayList<Double> finalVector = Calculate.getResultVector(target.getScenarioMatrixForBestTarget(), target.getBestTargetsAsArray());
            target.setFinalVector(finalVector);

            new ShowResultDialog(startWindow, target).display();
            reInitButtonsForAlog();
        }
    }

    private class RunSecond implements ActionListener {
        private StartWindow startWindow;

        public RunSecond(StartWindow startWindow) {
            this.startWindow = startWindow;
            starget = new SGlobalTarget("name");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            starget = new SActorsInput(startWindow, starget).display();
            addMatrix();
        }
    }

    private class OnMatrixSaved implements ActionListener {
        private StartWindow startWindow;

        public OnMatrixSaved(StartWindow startWindow) {
            this.startWindow = startWindow;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            starget.setActorsMatrix(actorTargetMatrix.getMatrix());
            starget = new STargetInput(startWindow, starget).display();
            starget = new STargetMatrix(startWindow, starget).display();
            starget = new SScenarioInput(startWindow, starget).display();
            starget = new SScenarioMatrix(startWindow, starget).display();
            starget.calculate();
            new SShowResult(startWindow, starget).display();
        }
    }
}