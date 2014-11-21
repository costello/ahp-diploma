package dialog;

import model.GlobalTarget;
import util.Calculate;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Sviat on 18.11.14.
 */
public class StartWindow extends JFrame {
    final static Logger log = Logger.getLogger(StartWindow.class);
    private static MigLayout migLayout = new MigLayout("ins 0, hidemode 3", "", "[][][]");

    private JTextField textFieldTarget;
    private JButton buttonAddActors;
    private JButton buttonActorTargetMatrixInputDone;
    private JPanel panelRoot;
    private JPanel panelMatrix;
    private JButton buttonStartScenarioInput;
    private JButton buttonSaveObject;
    private JButton buttonLoadObject;
    private GlobalTarget target;
    private RTable actorTargetMatrix;

    public static void main(String[] args) {
        log.debug("ok");
        StartWindow start = new StartWindow();
        start.setTitle("Диплом Горошка!");

        start.setSize(800, 400);
        start.setTitle("Диплом Горошка");
        start.setContentPane(start.panelRoot);
        start.setVisible(true);

        start.setLocationRelativeTo(null);

        start.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public StartWindow() throws HeadlessException {
        buttonAddActors.addActionListener(new OpenActorInputDialog(this));
        buttonActorTargetMatrixInputDone.addActionListener(new OpenActorsTargetsInput(this));
        buttonStartScenarioInput.addActionListener(new StartScenarioInput(this));

        buttonSaveObject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("globalTarget")));

                    oos.writeObject(target);
                    oos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        buttonLoadObject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("globalTarget")));
                    target = (GlobalTarget) ois.readObject();
                    ois.close();

                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });
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
        actorTargetMatrix = new RTable(target.getActorsNames());

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

            target.printResult(finalVector);
        }
    }
}