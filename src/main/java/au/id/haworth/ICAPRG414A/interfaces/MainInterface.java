package au.id.haworth.ICAPRG414A.interfaces;

import au.id.haworth.ICAPRG414A.Job;
import au.id.haworth.ICAPRG414A.JobRegister;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * The main interface is for users to view
 * information about jobs as well as add,
 * edit and deleted Jobs.
 *
 * @author Liam Haworth
 * @version 1.0
 */
public class MainInterface extends JFrame {

    private static MainInterface instance;
    private static final String[] tableHeaders = {"Job ID", "Client Name", "Job Details", "Job Price", "Date", "Completed", "Paid"};

    private JTable jobsList;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Failed to load native look and feel, continuing with Java default");
        }

        instance = new MainInterface();
        instance.setVisible(true);
    }

    public MainInterface() {
        setTitle("ICAPRG414A - Jobs List");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setSize(900, 350);
        setLocationRelativeTo(null);

        buildInterface();
    }

    private void saveToFile() {
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveFileChooser.setFileFilter(new JobsFileFilter());

        int result = saveFileChooser.showSaveDialog(this);

        if(result == JFileChooser.CANCEL_OPTION)
            return;

        JobRegister.saveJobsToFile(saveFileChooser.getSelectedFile());
    }

    private void loadFromFile() {
        JFileChooser loadFileChooser = new JFileChooser();
        loadFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        loadFileChooser.setFileFilter(new JobsFileFilter());

        int result = loadFileChooser.showOpenDialog(this);

        if(result == JFileChooser.CANCEL_OPTION)
            return;

        JobRegister.loadJobsFromFile(loadFileChooser.getSelectedFile());

        this.rebuildTable();
    }

    private void buildInterface() {
        JPanel mainContent = new JPanel();
        this.setContentPane(mainContent);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem saveDataMenuItem = new JMenuItem("Save To File");
        saveDataMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });

        JMenuItem loadDataMenuItem = new JMenuItem("Load From File");
        loadDataMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFromFile();
            }
        });

        fileMenu.add(saveDataMenuItem);
        fileMenu.add(loadDataMenuItem);
        menuBar.add(fileMenu);

        this.setJMenuBar(menuBar);

        JLabel windowTitle = new JLabel("Jobs List");
        windowTitle.setHorizontalAlignment(SwingConstants.CENTER);
        windowTitle.setFont(windowTitle.getFont().deriveFont(Font.BOLD, 22f));

        JScrollPane tablePane = new JScrollPane();
        jobsList = new JTable(new Object[][] {}, tableHeaders);
        jobsList.setFillsViewportHeight(true);
        rebuildTable();
        tablePane.getViewport().add(jobsList);

        JButton addJob = new JButton("Add New Job");
        addJob.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddJob addJobDialog = new AddJob(instance);
                addJobDialog.displayWindow();
            }
        });

        JButton editJob = new JButton("Edit Job");
        editJob.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int jobId = (int) jobsList.getModel().getValueAt(jobsList.getSelectedRow(), 0);

                EditJob editJobDialog = new EditJob(instance, jobId);
                editJobDialog.displayWindow();
            }
        });

        JButton deleteJob = new JButton("Delete Job");
        deleteJob.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int jobId = (int) jobsList.getModel().getValueAt(jobsList.getSelectedRow(), 0);
                int selection = JOptionPane.showConfirmDialog(instance, "Are you sure you want to delete?", "Job Deletion Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (selection == JOptionPane.YES_OPTION) {
                    JobRegister.removeJobFromRegister(jobId);
                    rebuildTable();
                }
            }
        });

        GroupLayout layout = new GroupLayout(this.getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(windowTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(tablePane)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(addJob, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editJob, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deleteJob, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(windowTitle)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(tablePane)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addJob)
                    .addComponent(editJob)
                    .addComponent(deleteJob)
                )
        );
    }

    public void rebuildTable() {
        DefaultTableModel dataModel = new DefaultTableModel(tableHeaders, 0) {
            public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };

        for (Job job : JobRegister.getJobList()) {
            dataModel.addRow(new Object[]{job.getJobID(), job.getName(), job.getJobDetails(), job.getPrice(), job.getDate(), job.isCompleted(), job.isPaid()});
        }

        this.jobsList.setModel(dataModel);
    }


    class JobsFileFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            if(f.isDirectory())
                return false;


            if(!f.getAbsolutePath().endsWith(".jobs"))
                return false;

            return true;
        }

        @Override
        public String getDescription() {
            return "Jobs File";
        }
    }
}
