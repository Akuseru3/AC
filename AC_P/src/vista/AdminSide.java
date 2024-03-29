/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import datos.Premio;
import datos.PlanBDManager;
import datos.SorteoBDManager;
import datos.Validate;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Kevin
 */
public class AdminSide extends javax.swing.JFrame {

    /**
     * Creates new form InicioGeneral
     */
    public AdminSide() {
        initComponents();
        sorteoTablaM.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                int row = sorteoTablaM.getSelectedRow();
                if(row>=0){
                    String name = sorteoTablaM.getValueAt(row, 1).toString();
                    String type = sorteoTablaM.getValueAt(row, 2).toString();
                    String fracc = sorteoTablaM.getValueAt(row, 3).toString();
                    String price = sorteoTablaM.getValueAt(row, 4).toString();
                    String date = sorteoTablaM.getValueAt(row, 5).toString();
                    sorteoModName.setText(name);
                    sorteoModType.setText(type);
                    sorteoModFracc.setText(fracc);
                    sorteoModPrice.setText(price);
                    String[] dateP = date.split("-");
                    sorteoModDay.setText(dateP[2]);
                    sorteoModMonth.setText(dateP[1]);
                    sorteoModYear.setText(dateP[0]);
                }
            }
        });
        planesTablaE.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                int row = planesTablaE.getSelectedRow();
                if(row>=0){
                    String name = planesTablaE.getValueAt(row, 0).toString();
                    PlanBDManager conector = new PlanBDManager();
                    DefaultTableModel modelo = conector.getPremios(name);
                    premiosTablaE.setModel(modelo);
                    premiosTablaM.setModel(modelo);
                }
            }
        });
        setVisibility();
        setFonts();
        fillSorteos();
        fillPlanes();
    }
    
    private void setVisibility(){
        sorteoPanel.setVisible(true);
        startSorteoPanel.setVisible(false);
        panelInGame.setVisible(false);
        planPanel.setVisible(false);
        reportInfoPanel.setVisible(false);
        reportPanel.setVisible(false);
        planA.setVisible(false);
        planM.setVisible(false);
        planE.setVisible(false);
        sorteoA.setVisible(false);
        sorteoM.setVisible(false);
        sorteoE.setVisible(false);
        btnEndGame.setVisible(false);
    }
    
    private void setFonts(){
        sorteoTablaE.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sorteoTablaM.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sorteoTablaA.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        planSorteoTablaA.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        planSorteoTablaM.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sorteoPlayTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loteriaTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        winnerTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    public void fillSorteos(){
        SorteoBDManager connector = new SorteoBDManager();
        String[] required = {"1","2"};
        DefaultTableModel modelo = connector.getSorteos(required,"");
        sorteoTablaE.setModel(modelo);
        sorteoTablaM.setModel(modelo);
        sorteoTablaA.setModel(modelo);
        required = new String[]{"1"};
        modelo = connector.getSorteos(required,"");
        planSorteoTablaA.setModel(modelo);
        planSorteoTablaM.setModel(modelo);
        modelo = connector.getPlanInnSorteo();
        sorteoPlayTable.setModel(modelo);
        modelo = connector.getPlanInnSorteoType("1");
        chanceTable.setModel(modelo);
        modelo = connector.getPlanInnSorteoType("2");
        loteriaTable.setModel(modelo);
    }
    
    public void fillPlanes(){
        PlanBDManager connector = new PlanBDManager();
        DefaultTableModel modelo = connector.getPlanes("1");
        planesTablaE.setModel(modelo);
    }
    
    private void startGame(int row){
        Thread t = new Thread(){
            Random rand = new Random();
            public void run(){
                ArrayList<Premio> ganadores = genGanadores();
                DefaultTableModel model = (DefaultTableModel) winnerTable.getModel();
                for(int i = 0;i<ganadores.size();i++){
                    gameInfo.setText("Generando Premio");
                    setInfo(0,0,ganadores.get(i).getGanancia());
                    long initTime = System.currentTimeMillis();
                    int cont = 0;
                    while(System.currentTimeMillis()-initTime<1500){
                        if(cont == 200){
                            setInfo(generateRandom(99),generateRandom(999),gamePrem.getText());
                            cont = 0;
                        }
                        cont+=1;
                    }
                    setInfoStr(ganadores.get(i).getNombre(), ganadores.get(i).getCantidad(), ganadores.get(i).getGanancia());
                    gameInfo.setText("¡Ganador!");
                    model.addRow(new Object[]{ganadores.get(i).getNombre(), ganadores.get(i).getCantidad(), ganadores.get(i).getGanancia()});
                    initTime = System.currentTimeMillis();
                    while(System.currentTimeMillis()-initTime<2000){
                        
                    }
                }
                btnEndGame.setVisible(true);
                this.interrupt();
            }
            
            private int generateRandom(int end){
                int n = rand.nextInt(end);
                n+=1;
                return n;
            }
            
            private void setInfoStr(String val1,String val2,String val3){
                gameNum.setText(val1);
                gameSer.setText(val2);
                gamePrem.setText(val3);
            }
            
            private void setInfo(int val1,int val2,String val3){
                gameNum.setText(String.valueOf(val1));
                gameSer.setText(String.valueOf(val2));
                gamePrem.setText(val3);
            }
            private ArrayList<Premio> genGanadores(){
                String idSort = sorteoPlayTable.getModel().getValueAt(row, 0).toString();
                String value = sorteoPlayTable.getModel().getValueAt(row, 5).toString();
                PlanBDManager connector = new PlanBDManager();
                ArrayList<Premio> prices = Premio.getTablePrices(connector.getPremios(value));
                ArrayList<Premio> ganadores = Premio.generateWinners(idSort, prices);
                Collections.reverse(ganadores);
                return ganadores;
            }
        };
        t.start();
    }
    
    private void reportGenerator(String idSorteo,String name,String type,String price,String date,String plan,String total){
        reportTitle.setText("Reporte del sorteo N. "+idSorteo);
        reportCode.setText("Codigo: "+idSorteo);
        reportName.setText("Nombre: "+name);
        reportType.setText("Tipo: "+type);
        reportPrice.setText("Precio: "+price);
        reportDate.setText("Fecha del sorteo: "+date);
        reportPlan.setText("Plan de Premios: "+plan);
        reportTotal.setText("Total de premios: "+total);
        PlanBDManager planM = new PlanBDManager();
        reportPremioTable.setModel(planM.getPremios(plan));
        SorteoBDManager sortM = new SorteoBDManager();
        reportWinnerTable.setModel(sortM.getWinners(idSorteo));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton13 = new javax.swing.JButton();
        btnStartG = new javax.swing.JButton();
        btnReports = new javax.swing.JButton();
        btnGestionS = new javax.swing.JButton();
        btnGestionP = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        startSorteoPanel = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        infoMess = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        panelInGame = new javax.swing.JPanel();
        gameInfo = new javax.swing.JLabel();
        bolaLot = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        gameNum = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        gameSer = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        gamePrem = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        winnerTable = new javax.swing.JTable();
        btnEndGame = new javax.swing.JButton();
        btnPlayGame = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane13 = new javax.swing.JScrollPane();
        sorteoPlayTable = new javax.swing.JTable();
        jLabel70 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        reportInfoPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        reportPremioTable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        reportWinnerTable = new javax.swing.JTable();
        reportType = new javax.swing.JLabel();
        reportDate = new javax.swing.JLabel();
        reportName = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        reportPlan = new javax.swing.JLabel();
        reportPrice = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        reportCode = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        reportTitle = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        reportTotal = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        reportPanel = new javax.swing.JPanel();
        btnGenerateReportC = new javax.swing.JButton();
        btnGenerateReportL = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        chanceTable = new javax.swing.JTable();
        jScrollPane9 = new javax.swing.JScrollPane();
        loteriaTable = new javax.swing.JTable();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel63 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        planPanel = new javax.swing.JPanel();
        delPlanB = new javax.swing.JButton();
        addPlanB = new javax.swing.JButton();
        jLabel61 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        planE = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        planesTablaE = new javax.swing.JTable();
        jLabel79 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        premiosTablaE = new javax.swing.JTable();
        btnGoModPlan = new javax.swing.JButton();
        btnEliminarPlan = new javax.swing.JButton();
        jLabel53 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        planM = new javax.swing.JPanel();
        jLabel80 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        planSorteoTablaM = new javax.swing.JTable();
        btnSelectSorteoM = new javax.swing.JButton();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        planModOldNum = new javax.swing.JLabel();
        planModName = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        planModNum = new javax.swing.JLabel();
        premioAddNameM = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        premioAddCantM = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        premioAddAmountM = new javax.swing.JTextField();
        btnAgregarPremioM = new javax.swing.JButton();
        btnEliminarPremioM = new javax.swing.JButton();
        jScrollPane16 = new javax.swing.JScrollPane();
        premiosTablaM = new javax.swing.JTable();
        jLabel83 = new javax.swing.JLabel();
        btnModPlan = new javax.swing.JButton();
        jLabel59 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        planA = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        planAddNum = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        planSorteoTablaA = new javax.swing.JTable();
        jScrollPane11 = new javax.swing.JScrollPane();
        premiosTablaA = new javax.swing.JTable();
        jLabel37 = new javax.swing.JLabel();
        btnSelectSorteo = new javax.swing.JButton();
        jLabel43 = new javax.swing.JLabel();
        planAddName = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        premioAddName = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        premioAddCant = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        premioAddAmount = new javax.swing.JTextField();
        btnAgregarPremio = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        btnEliminarPremio = new javax.swing.JButton();
        btnCrearPlan = new javax.swing.JButton();
        jLabel78 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        sorteoPanel = new javax.swing.JPanel();
        delSortB = new javax.swing.JButton();
        modSortB = new javax.swing.JButton();
        addSortB = new javax.swing.JButton();
        jLabel60 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        sorteoE = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        sorteoTablaE = new javax.swing.JTable();
        btnDeleteSorteo = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        sorteoM = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        sorteoModType = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        sorteoTablaM = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        sorteoModName = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        sorteoModFracc = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        sorteoModPrice = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        sorteoModYear = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        sorteoModMonth = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        sorteoModDay = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        btnModSorteo = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        sorteoA = new javax.swing.JPanel();
        sorteoAddErr = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sorteoTablaA = new javax.swing.JTable();
        sorteoAddName = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        sorteoAddType = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        sorteoAddFracc = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        sorteoAddPrice = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        sorteoAddDay = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        sorteoAddMonth = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        sorteoAddYear = new javax.swing.JTextField();
        btnAddSorteo = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton13.setBackground(new java.awt.Color(21, 57, 90));
        jButton13.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jButton13.setForeground(new java.awt.Color(255, 255, 255));
        jButton13.setText("VOLVER AL INICIO");
        jButton13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(21, 41, 65), 1, true));
        jButton13.setContentAreaFilled(false);
        jButton13.setFocusPainted(false);
        jButton13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseChangeEnt(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mouseChangeEx(evt);
            }
        });
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 0, 120, 30));

        btnStartG.setBackground(new java.awt.Color(21, 57, 90));
        btnStartG.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        btnStartG.setForeground(new java.awt.Color(255, 255, 255));
        btnStartG.setText("INICIAR SORTEO");
        btnStartG.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(21, 41, 65), 1, true));
        btnStartG.setContentAreaFilled(false);
        btnStartG.setFocusPainted(false);
        btnStartG.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseChangeEnt(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mouseChangeEx(evt);
            }
        });
        btnStartG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartGActionPerformed(evt);
            }
        });
        getContentPane().add(btnStartG, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 30, 150, 50));

        btnReports.setBackground(new java.awt.Color(21, 57, 90));
        btnReports.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        btnReports.setForeground(new java.awt.Color(255, 255, 255));
        btnReports.setText("REPORTES");
        btnReports.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(21, 41, 65), 1, true));
        btnReports.setContentAreaFilled(false);
        btnReports.setFocusPainted(false);
        btnReports.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseChangeEnt(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mouseChangeEx(evt);
            }
        });
        btnReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportsActionPerformed(evt);
            }
        });
        getContentPane().add(btnReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 30, 130, 50));

        btnGestionS.setBackground(new java.awt.Color(21, 57, 90));
        btnGestionS.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        btnGestionS.setForeground(new java.awt.Color(255, 255, 255));
        btnGestionS.setText("GESTIÓN DE SORTEOS");
        btnGestionS.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(21, 41, 65)));
        btnGestionS.setContentAreaFilled(false);
        btnGestionS.setFocusPainted(false);
        btnGestionS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseChangeEnt(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mouseChangeEx(evt);
            }
        });
        btnGestionS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGestionSActionPerformed(evt);
            }
        });
        getContentPane().add(btnGestionS, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 190, 50));

        btnGestionP.setBackground(new java.awt.Color(21, 57, 90));
        btnGestionP.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        btnGestionP.setForeground(new java.awt.Color(255, 255, 255));
        btnGestionP.setText("GESTIÓN DE PLANES");
        btnGestionP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(21, 41, 65)));
        btnGestionP.setContentAreaFilled(false);
        btnGestionP.setFocusPainted(false);
        btnGestionP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseChangeEnt(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mouseChangeEx(evt);
            }
        });
        btnGestionP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGestionPActionPerformed(evt);
            }
        });
        getContentPane().add(btnGestionP, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 30, 160, 50));

        jLabel3.setBackground(new java.awt.Color(21, 57, 90));
        jLabel3.setOpaque(true);
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 1260, 50));

        jLabel2.setBackground(new java.awt.Color(16, 47, 75));
        jLabel2.setOpaque(true);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -60, 1260, 150));

        startSorteoPanel.setOpaque(false);
        startSorteoPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel54.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(255, 255, 255));
        jLabel54.setText("Sorteo en juego");
        startSorteoPanel.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 70, -1, -1));

        jLabel76.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(255, 255, 255));
        jLabel76.setText("Sorteos con plan de premios sin jugar");
        startSorteoPanel.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 70, -1, -1));

        infoMess.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        infoMess.setForeground(new java.awt.Color(204, 204, 204));
        infoMess.setText("Actualmente no hay un sorteo siendo jugado");
        startSorteoPanel.add(infoMess, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 330, -1, -1));

        jLabel100.setBackground(new java.awt.Color(255, 51, 51));
        jLabel100.setOpaque(true);
        startSorteoPanel.add(jLabel100, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 1100, 50));

        panelInGame.setBackground(new java.awt.Color(255, 255, 255));
        panelInGame.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        gameInfo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        gameInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gameInfo.setText("Generando Premio");
        panelInGame.add(gameInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(19, 160, 460, 30));

        bolaLot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loteria2.gif"))); // NOI18N
        panelInGame.add(bolaLot, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 120, 120));

        jLabel77.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/viejoDance.gif"))); // NOI18N
        panelInGame.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 70, 110));

        jLabel55.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel55.setText("Número Ganador");
        panelInGame.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, -1, -1));

        gameNum.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        gameNum.setText("0");
        panelInGame.add(gameNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 120, -1));

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel56.setText("Serie Ganador");
        panelInGame.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 200, -1, -1));

        gameSer.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        gameSer.setText("0");
        panelInGame.add(gameSer, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 230, 100, -1));

        jLabel68.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel68.setText("Premio");
        panelInGame.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 200, -1, -1));

        gamePrem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        gamePrem.setText("0");
        panelInGame.add(gamePrem, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 230, 120, -1));

        winnerTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        winnerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numero", "Serie", "Premio"
            }
        ));
        winnerTable.setRowHeight(24);
        jScrollPane14.setViewportView(winnerTable);

        panelInGame.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 270, 180));

        btnEndGame.setBackground(new java.awt.Color(21, 57, 90));
        btnEndGame.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnEndGame.setForeground(new java.awt.Color(255, 255, 255));
        btnEndGame.setText("TERMINAR");
        btnEndGame.setBorderPainted(false);
        btnEndGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndGameActionPerformed(evt);
            }
        });
        panelInGame.add(btnEndGame, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 330, 120, 40));

        startSorteoPanel.add(panelInGame, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 100, 500, 470));

        btnPlayGame.setBackground(new java.awt.Color(255, 51, 51));
        btnPlayGame.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnPlayGame.setForeground(new java.awt.Color(255, 255, 255));
        btnPlayGame.setText("JUGAR SORTEO");
        btnPlayGame.setBorderPainted(false);
        btnPlayGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayGameActionPerformed(evt);
            }
        });
        startSorteoPanel.add(btnPlayGame, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 490, 150, 50));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        startSorteoPanel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 60, 10, 520));

        sorteoPlayTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        sorteoPlayTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Numero", "Nombre", "Tipo", "Fracciones", "Precio", "Fecha"
            }
        ));
        sorteoPlayTable.setRowHeight(24);
        jScrollPane13.setViewportView(sorteoPlayTable);

        startSorteoPanel.add(jScrollPane13, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 510, 330));

        jLabel70.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel70.setForeground(new java.awt.Color(255, 255, 255));
        jLabel70.setText("Jugar Sorteo");
        startSorteoPanel.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, -1));

        jLabel101.setBackground(new java.awt.Color(255, 255, 255));
        jLabel101.setOpaque(true);
        startSorteoPanel.add(jLabel101, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 1100, 520));

        getContentPane().add(startSorteoPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 1260, 640));

        reportInfoPanel.setOpaque(false);
        reportInfoPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        reportPremioTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(reportPremioTable);

        reportInfoPanel.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 120, 530, 370));

        reportWinnerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(reportWinnerTable);

        reportInfoPanel.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 300, 400, 250));

        reportType.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportType.setText("Tipo");
        reportInfoPanel.add(reportType, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, -1, -1));

        reportDate.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportDate.setText("Fecha del Sorteo");
        reportInfoPanel.add(reportDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 190, -1, -1));

        reportName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportName.setText("Nombre");
        reportInfoPanel.add(reportName, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, -1, -1));

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel44.setText("Ganadores");
        reportInfoPanel.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 265, -1, 30));

        reportPlan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportPlan.setText("Plan de Premios");
        reportInfoPanel.add(reportPlan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 220, -1, -1));

        reportPrice.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportPrice.setText("Precio");
        reportInfoPanel.add(reportPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, -1, -1));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel45.setText("Premios");
        reportInfoPanel.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 90, 200, -1));

        reportCode.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportCode.setText("Codigo");
        reportInfoPanel.add(reportCode, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, -1, -1));

        jButton11.setBackground(new java.awt.Color(21, 57, 90));
        jButton11.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("VOLVER");
        jButton11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(21, 41, 65)));
        jButton11.setBorderPainted(false);
        jButton11.setFocusPainted(false);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        reportInfoPanel.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 540, 120, 30));

        reportTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportTitle.setForeground(new java.awt.Color(255, 255, 255));
        reportTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        reportTitle.setText("Chances");
        reportInfoPanel.add(reportTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 1100, 30));

        jLabel102.setBackground(new java.awt.Color(255, 51, 51));
        jLabel102.setOpaque(true);
        reportInfoPanel.add(jLabel102, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 1100, 40));

        reportTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportTotal.setText("Total de Premios");
        reportInfoPanel.add(reportTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 490, 530, 40));

        jLabel94.setBackground(new java.awt.Color(204, 204, 204));
        jLabel94.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel94.setOpaque(true);
        reportInfoPanel.add(jLabel94, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 420, 180));

        jLabel103.setBackground(new java.awt.Color(255, 255, 255));
        jLabel103.setOpaque(true);
        reportInfoPanel.add(jLabel103, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 1100, 560));

        getContentPane().add(reportInfoPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 1260, 640));

        reportPanel.setOpaque(false);
        reportPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnGenerateReportC.setBackground(new java.awt.Color(21, 57, 90));
        btnGenerateReportC.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        btnGenerateReportC.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerateReportC.setText("GENERAR REPORTE");
        btnGenerateReportC.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(21, 41, 65)));
        btnGenerateReportC.setBorderPainted(false);
        btnGenerateReportC.setFocusPainted(false);
        btnGenerateReportC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateReportCActionPerformed(evt);
            }
        });
        reportPanel.add(btnGenerateReportC, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 500, 160, 50));

        btnGenerateReportL.setBackground(new java.awt.Color(21, 57, 90));
        btnGenerateReportL.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        btnGenerateReportL.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerateReportL.setText("GENERAR REPORTE");
        btnGenerateReportL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(21, 41, 65)));
        btnGenerateReportL.setBorderPainted(false);
        btnGenerateReportL.setFocusPainted(false);
        btnGenerateReportL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateReportLActionPerformed(evt);
            }
        });
        reportPanel.add(btnGenerateReportL, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 500, 160, 50));

        chanceTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        chanceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Numero de sorteo", "Nombre de sorteo", "Cantidad de fracciones", "Precio", "Fecha"
            }
        ));
        chanceTable.setRowHeight(24);
        jScrollPane10.setViewportView(chanceTable);

        reportPanel.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 140, 530, 340));

        loteriaTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        loteriaTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Numero de sorteo", "Nombre de sorteo", "Cantidad de fracciones", "Precio", "Fecha"
            }
        ));
        loteriaTable.setRowHeight(24);
        jScrollPane9.setViewportView(loteriaTable);

        reportPanel.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 140, 530, 340));

        jLabel64.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(255, 255, 255));
        jLabel64.setText("Chances");
        reportPanel.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 60, -1, 30));

        jLabel65.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(255, 255, 255));
        jLabel65.setText("Lotería");
        reportPanel.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, -1, 30));

        jLabel98.setBackground(new java.awt.Color(255, 51, 51));
        jLabel98.setOpaque(true);
        reportPanel.add(jLabel98, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 1100, 40));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        reportPanel.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 70, 30, 510));

        jLabel63.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(255, 255, 255));
        jLabel63.setText("Reportes");
        reportPanel.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, -1));

        jLabel99.setBackground(new java.awt.Color(255, 255, 255));
        jLabel99.setOpaque(true);
        reportPanel.add(jLabel99, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 1100, 520));

        getContentPane().add(reportPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 1260, 640));

        planPanel.setOpaque(false);
        planPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        delPlanB.setBackground(new java.awt.Color(21, 57, 90));
        delPlanB.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        delPlanB.setForeground(new java.awt.Color(255, 255, 255));
        delPlanB.setText("MODIFICAR O ELIMINAR");
        delPlanB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(21, 41, 65)));
        delPlanB.setContentAreaFilled(false);
        delPlanB.setFocusPainted(false);
        delPlanB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delPlanBActionPerformed(evt);
            }
        });
        planPanel.add(delPlanB, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 0, 190, 30));

        addPlanB.setBackground(new java.awt.Color(21, 57, 90));
        addPlanB.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addPlanB.setForeground(new java.awt.Color(255, 255, 255));
        addPlanB.setText("AÑADIR");
        addPlanB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(21, 41, 65)));
        addPlanB.setContentAreaFilled(false);
        addPlanB.setFocusPainted(false);
        addPlanB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPlanBActionPerformed(evt);
            }
        });
        planPanel.add(addPlanB, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 110, 30));

        jLabel61.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(255, 255, 255));
        jLabel61.setText("PLANES");
        planPanel.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 0, -1, -1));

        jLabel6.setBackground(new java.awt.Color(21, 57, 90));
        jLabel6.setOpaque(true);
        planPanel.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 1150, 30));

        planE.setOpaque(false);
        planE.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        planesTablaE.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        planesTablaE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Numero de sorteo", "Nombre de sorteo", "Tipo Sorteo", "Cantidad de fracciones", "Precio", "Fecha"
            }
        ));
        planesTablaE.setRowHeight(24);
        jScrollPane8.setViewportView(planesTablaE);

        planE.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 150, 620, 340));

        jLabel79.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel79.setText("Premios");
        planE.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 110, -1, -1));

        premiosTablaE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cantidad", "Ganancia"
            }
        ));
        jScrollPane12.setViewportView(premiosTablaE);

        planE.add(jScrollPane12, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 150, 310, 340));

        btnGoModPlan.setBackground(new java.awt.Color(21, 57, 90));
        btnGoModPlan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnGoModPlan.setForeground(new java.awt.Color(255, 255, 255));
        btnGoModPlan.setText("Modificar plan");
        btnGoModPlan.setContentAreaFilled(false);
        btnGoModPlan.setOpaque(true);
        btnGoModPlan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoModPlanActionPerformed(evt);
            }
        });
        planE.add(btnGoModPlan, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 500, -1, -1));

        btnEliminarPlan.setBackground(new java.awt.Color(21, 57, 90));
        btnEliminarPlan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnEliminarPlan.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarPlan.setText("Eliminar plan");
        btnEliminarPlan.setContentAreaFilled(false);
        btnEliminarPlan.setOpaque(true);
        btnEliminarPlan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPlanActionPerformed(evt);
            }
        });
        planE.add(btnEliminarPlan, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 500, -1, -1));

        jLabel53.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel53.setText("Planes actuales");
        planE.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, -1, -1));

        jLabel66.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(255, 255, 255));
        jLabel66.setText("Eliminar plan de premios");
        planE.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jLabel67.setBackground(new java.awt.Color(255, 51, 51));
        jLabel67.setOpaque(true);
        planE.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 40));

        jLabel74.setBackground(new java.awt.Color(255, 255, 255));
        jLabel74.setOpaque(true);
        planE.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 500));

        planPanel.add(planE, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 1180, 590));

        planM.setOpaque(false);
        planM.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel80.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel80.setText("Sorteos sin plan de premios");
        planM.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, -1, -1));

        planSorteoTablaM.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        planSorteoTablaM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Numero de plan", "Nombre de plan", "Cantidad de fracciones", "Ganancia"
            }
        ));
        planSorteoTablaM.setRowHeight(24);
        jScrollPane15.setViewportView(planSorteoTablaM);

        planM.add(jScrollPane15, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, 470, 310));

        btnSelectSorteoM.setBackground(new java.awt.Color(21, 57, 90));
        btnSelectSorteoM.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSelectSorteoM.setForeground(new java.awt.Color(255, 255, 255));
        btnSelectSorteoM.setText("Seleccionar sorteo");
        btnSelectSorteoM.setContentAreaFilled(false);
        btnSelectSorteoM.setOpaque(true);
        btnSelectSorteoM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectSorteoMActionPerformed(evt);
            }
        });
        planM.add(btnSelectSorteoM, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 480, -1, -1));

        jLabel90.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel90.setText("Nuevo");
        planM.add(jLabel90, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 160, 80, -1));

        jLabel91.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel91.setText("Viejo");
        planM.add(jLabel91, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 120, 80, -1));

        jLabel89.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel89.setText("Sorteo N.");
        planM.add(jLabel89, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 120, 80, -1));

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel49.setText("Agregados");
        planM.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 220, -1, -1));

        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel50.setText("Información de premios");
        planM.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 200, -1, -1));

        planModOldNum.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        planModOldNum.setText("--");
        planM.add(planModOldNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 120, 40, -1));

        planModName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        planModName.setText("Nombre del plan");
        planM.add(planModName, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 140, -1, -1));

        jLabel88.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel88.setText("Nombre del plan:");
        planM.add(jLabel88, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 140, -1, -1));

        jLabel82.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel82.setText("Sorteo N.");
        planM.add(jLabel82, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 160, 80, -1));

        planModNum.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        planModNum.setText("--");
        planM.add(planModNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 160, 40, -1));

        premioAddNameM.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        planM.add(premioAddNameM, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 260, 130, 20));

        jLabel84.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel84.setText("Nombre");
        planM.add(jLabel84, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 260, -1, -1));

        jLabel85.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel85.setText("Cantidad");
        planM.add(jLabel85, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 310, -1, -1));

        premioAddCantM.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        planM.add(premioAddCantM, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 310, 130, 20));

        jLabel86.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel86.setText("Ganancia");
        planM.add(jLabel86, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 360, -1, -1));

        premioAddAmountM.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        planM.add(premioAddAmountM, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 360, 130, 20));

        btnAgregarPremioM.setBackground(new java.awt.Color(21, 57, 90));
        btnAgregarPremioM.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAgregarPremioM.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarPremioM.setText("Agregar");
        btnAgregarPremioM.setContentAreaFilled(false);
        btnAgregarPremioM.setOpaque(true);
        btnAgregarPremioM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarPremioMActionPerformed(evt);
            }
        });
        planM.add(btnAgregarPremioM, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 420, -1, -1));

        btnEliminarPremioM.setBackground(new java.awt.Color(21, 57, 90));
        btnEliminarPremioM.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminarPremioM.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarPremioM.setText("Eliminar");
        btnEliminarPremioM.setContentAreaFilled(false);
        btnEliminarPremioM.setOpaque(true);
        btnEliminarPremioM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPremioMActionPerformed(evt);
            }
        });
        planM.add(btnEliminarPremioM, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 420, 90, 30));

        premiosTablaM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Cantidad", "Ganancia"
            }
        ));
        jScrollPane16.setViewportView(premiosTablaM);

        planM.add(jScrollPane16, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 240, 250, 160));

        jLabel83.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        planM.add(jLabel83, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 200, 540, 270));

        btnModPlan.setBackground(new java.awt.Color(21, 57, 90));
        btnModPlan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnModPlan.setForeground(new java.awt.Color(255, 255, 255));
        btnModPlan.setText("Modificar plan");
        btnModPlan.setContentAreaFilled(false);
        btnModPlan.setOpaque(true);
        btnModPlan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModPlanActionPerformed(evt);
            }
        });
        planM.add(btnModPlan, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 490, -1, -1));

        jLabel59.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(255, 255, 255));
        jLabel59.setText("Modificar plan de premios");
        planM.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jLabel62.setBackground(new java.awt.Color(255, 51, 51));
        jLabel62.setOpaque(true);
        planM.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 40));

        jLabel73.setBackground(new java.awt.Color(255, 255, 255));
        jLabel73.setOpaque(true);
        planM.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 500));

        planPanel.add(planM, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 1180, 590));

        planA.setOpaque(false);
        planA.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel36.setText("Nombre del plan");
        planA.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 140, -1, -1));

        jLabel52.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel52.setText("Sorteos sin plan de premios");
        planA.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, -1, -1));

        planAddNum.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        planAddNum.setText("--");
        planA.add(planAddNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 140, 40, -1));

        planSorteoTablaA.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        planSorteoTablaA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Numero de plan", "Nombre de plan", "Cantidad de fracciones", "Ganancia"
            }
        ));
        planSorteoTablaA.setRowHeight(24);
        jScrollPane7.setViewportView(planSorteoTablaA);

        planA.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, 470, 310));

        premiosTablaA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Cantidad", "Ganancia"
            }
        ));
        jScrollPane11.setViewportView(premiosTablaA);

        planA.add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 240, 270, 160));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel37.setText("Sorteo N.");
        planA.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 140, 80, -1));

        btnSelectSorteo.setBackground(new java.awt.Color(21, 57, 90));
        btnSelectSorteo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSelectSorteo.setForeground(new java.awt.Color(255, 255, 255));
        btnSelectSorteo.setText("Seleccionar sorteo");
        btnSelectSorteo.setContentAreaFilled(false);
        btnSelectSorteo.setOpaque(true);
        btnSelectSorteo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectSorteoActionPerformed(evt);
            }
        });
        planA.add(btnSelectSorteo, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 480, -1, -1));

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel43.setText("Agregados");
        planA.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 220, -1, -1));

        planAddName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        planA.add(planAddName, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 140, 150, 30));

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel38.setText("Nombre");
        planA.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 250, -1, -1));

        premioAddName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        planA.add(premioAddName, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 250, 130, 20));

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel39.setText("Cantidad");
        planA.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 300, -1, -1));

        premioAddCant.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        planA.add(premioAddCant, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 300, 130, 20));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel40.setText("Ganancia");
        planA.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 350, -1, -1));

        premioAddAmount.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        planA.add(premioAddAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 350, 130, 20));

        btnAgregarPremio.setBackground(new java.awt.Color(21, 57, 90));
        btnAgregarPremio.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAgregarPremio.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarPremio.setText("Agregar");
        btnAgregarPremio.setContentAreaFilled(false);
        btnAgregarPremio.setOpaque(true);
        btnAgregarPremio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarPremioActionPerformed(evt);
            }
        });
        planA.add(btnAgregarPremio, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 420, -1, -1));

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel41.setText("Información de premios");
        planA.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 200, -1, -1));

        btnEliminarPremio.setBackground(new java.awt.Color(21, 57, 90));
        btnEliminarPremio.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminarPremio.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarPremio.setText("Eliminar");
        btnEliminarPremio.setContentAreaFilled(false);
        btnEliminarPremio.setOpaque(true);
        btnEliminarPremio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPremioActionPerformed(evt);
            }
        });
        planA.add(btnEliminarPremio, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 420, 90, 30));

        btnCrearPlan.setBackground(new java.awt.Color(21, 57, 90));
        btnCrearPlan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnCrearPlan.setForeground(new java.awt.Color(255, 255, 255));
        btnCrearPlan.setText("Crear plan de premio");
        btnCrearPlan.setContentAreaFilled(false);
        btnCrearPlan.setOpaque(true);
        btnCrearPlan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearPlanActionPerformed(evt);
            }
        });
        planA.add(btnCrearPlan, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 490, -1, -1));

        jLabel78.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        planA.add(jLabel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 200, 540, 270));

        jLabel57.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(255, 255, 255));
        jLabel57.setText("Agregar nuevo plan de premios");
        planA.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jLabel58.setBackground(new java.awt.Color(255, 51, 51));
        jLabel58.setOpaque(true);
        planA.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 40));

        jLabel72.setBackground(new java.awt.Color(255, 255, 255));
        jLabel72.setOpaque(true);
        planA.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 500));

        planPanel.add(planA, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 1180, 590));

        getContentPane().add(planPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 1260, 640));

        sorteoPanel.setOpaque(false);
        sorteoPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        delSortB.setBackground(new java.awt.Color(21, 57, 90));
        delSortB.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        delSortB.setForeground(new java.awt.Color(255, 255, 255));
        delSortB.setText("ELIMINAR");
        delSortB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(21, 41, 65)));
        delSortB.setContentAreaFilled(false);
        delSortB.setFocusPainted(false);
        delSortB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delSortBActionPerformed(evt);
            }
        });
        sorteoPanel.add(delSortB, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 0, 100, 30));

        modSortB.setBackground(new java.awt.Color(21, 57, 90));
        modSortB.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        modSortB.setForeground(new java.awt.Color(255, 255, 255));
        modSortB.setText("MODIFICAR");
        modSortB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(21, 41, 65)));
        modSortB.setContentAreaFilled(false);
        modSortB.setFocusPainted(false);
        modSortB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modSortBActionPerformed(evt);
            }
        });
        sorteoPanel.add(modSortB, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 0, 120, 30));

        addSortB.setBackground(new java.awt.Color(21, 57, 90));
        addSortB.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addSortB.setForeground(new java.awt.Color(255, 255, 255));
        addSortB.setText("AÑADIR");
        addSortB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(21, 41, 65)));
        addSortB.setContentAreaFilled(false);
        addSortB.setFocusPainted(false);
        addSortB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSortBActionPerformed(evt);
            }
        });
        sorteoPanel.add(addSortB, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 110, 30));

        jLabel60.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(255, 255, 255));
        jLabel60.setText("SORTEOS");
        sorteoPanel.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel5.setBackground(new java.awt.Color(21, 57, 90));
        jLabel5.setOpaque(true);
        sorteoPanel.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 1150, 30));

        sorteoE.setOpaque(false);
        sorteoE.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel35.setText("Seleccione el sorteo que desea eliminar");
        sorteoE.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 110, -1, -1));

        sorteoTablaE.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        sorteoTablaE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Numero de sorteo", "Nombre de sorteo", "Tipo Sorteo", "Cantidad de fracciones", "Precio", "Fecha"
            }
        ));
        sorteoTablaE.setRowHeight(24);
        sorteoTablaE.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(sorteoTablaE);

        sorteoE.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, 700, 340));

        btnDeleteSorteo.setBackground(new java.awt.Color(21, 57, 90));
        btnDeleteSorteo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnDeleteSorteo.setForeground(new java.awt.Color(255, 255, 255));
        btnDeleteSorteo.setText("Eliminar sorteo");
        btnDeleteSorteo.setContentAreaFilled(false);
        btnDeleteSorteo.setOpaque(true);
        btnDeleteSorteo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSorteoActionPerformed(evt);
            }
        });
        sorteoE.add(btnDeleteSorteo, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 500, -1, -1));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(255, 255, 255));
        jLabel46.setText("Eliminar sorteo");
        sorteoE.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jLabel47.setBackground(new java.awt.Color(255, 51, 51));
        jLabel47.setOpaque(true);
        sorteoE.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 40));

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setOpaque(true);
        sorteoE.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 500));

        sorteoPanel.add(sorteoE, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 1170, 590));

        sorteoM.setOpaque(false);
        sorteoM.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setText("Seleccione el sorteo que desea modificar");
        sorteoM.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, -1, -1));

        sorteoModType.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        sorteoM.add(sorteoModType, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 220, -1, -1));

        sorteoTablaM.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        sorteoTablaM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Numero de sorteo", "Nombre de sorteo", "Tipo Sorteo", "Cantidad de fracciones", "Precio", "Fecha"
            }
        ));
        sorteoTablaM.setRowHeight(24);
        sorteoTablaM.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(sorteoTablaM);

        sorteoM.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, 580, 340));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel23.setText("Nombre de sorteo");
        sorteoM.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 150, -1, -1));

        sorteoModName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoM.add(sorteoModName, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 150, 150, 30));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setText("Tipo de sorteo");
        sorteoM.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 220, -1, -1));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel25.setText("Cantidad de fracciones");
        sorteoM.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 290, -1, -1));

        sorteoModFracc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoM.add(sorteoModFracc, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 290, 150, 30));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setText("Precio de sorteo");
        sorteoM.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 360, -1, -1));

        sorteoModPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoM.add(sorteoModPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 360, 150, 30));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel27.setText("Fecha de sorteo");
        sorteoM.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 430, -1, -1));

        sorteoModYear.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoM.add(sorteoModYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 430, 50, 30));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(153, 153, 153));
        jLabel28.setText("Año");
        sorteoM.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 400, 40, 30));

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(153, 153, 153));
        jLabel29.setText("--");
        sorteoM.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 430, 10, 30));

        sorteoModMonth.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoM.add(sorteoModMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 430, 30, 30));

        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(153, 153, 153));
        jLabel30.setText("Mes");
        sorteoM.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 400, 40, 30));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(153, 153, 153));
        jLabel31.setText("--");
        sorteoM.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 430, 10, 30));

        sorteoModDay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoM.add(sorteoModDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 430, 30, 30));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(153, 153, 153));
        jLabel33.setText("Día");
        sorteoM.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 400, 40, 30));

        btnModSorteo.setBackground(new java.awt.Color(21, 57, 90));
        btnModSorteo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnModSorteo.setForeground(new java.awt.Color(255, 255, 255));
        btnModSorteo.setText("Modificar sorteo");
        btnModSorteo.setContentAreaFilled(false);
        btnModSorteo.setOpaque(true);
        btnModSorteo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModSorteoActionPerformed(evt);
            }
        });
        sorteoM.add(btnModSorteo, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 490, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Modificar sorteo");
        sorteoM.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jLabel11.setBackground(new java.awt.Color(255, 51, 51));
        jLabel11.setOpaque(true);
        sorteoM.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 40));

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setOpaque(true);
        sorteoM.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 500));

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel42.setText("Tipo de sorteo");
        sorteoM.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 220, -1, -1));

        sorteoPanel.add(sorteoM, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 1200, 590));

        sorteoA.setOpaque(false);
        sorteoA.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sorteoAddErr.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        sorteoAddErr.setForeground(new java.awt.Color(255, 255, 255));
        sorteoAddErr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sorteoA.add(sorteoAddErr, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 65, 1080, 30));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Agregar nuevo sorteo");
        sorteoA.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jLabel8.setBackground(new java.awt.Color(255, 51, 51));
        jLabel8.setOpaque(true);
        sorteoA.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 40));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Nombre de sorteo");
        sorteoA.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 140, -1, -1));

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel34.setText("Sorteos actuales");
        sorteoA.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 140, -1, -1));

        sorteoTablaA.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        sorteoTablaA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Numero de sorteo", "Nombre de sorteo", "Tipo Sorteo", "Cantidad de fracciones", "Precio", "Fecha"
            }
        ));
        sorteoTablaA.setRowHeight(24);
        sorteoTablaA.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(sorteoTablaA);

        sorteoA.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 170, 590, 340));

        sorteoAddName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoA.add(sorteoAddName, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 140, 150, 30));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Tipo de sorteo");
        sorteoA.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 210, -1, -1));

        sorteoAddType.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoAddType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Loteria", "Chances" }));
        sorteoA.add(sorteoAddType, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 210, 150, 30));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Cantidad de fracciones");
        sorteoA.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 280, -1, -1));

        sorteoAddFracc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoA.add(sorteoAddFracc, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 280, 150, 30));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Precio de sorteo");
        sorteoA.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 350, -1, -1));

        sorteoAddPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoA.add(sorteoAddPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 350, 150, 30));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setText("Fecha de sorteo");
        sorteoA.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 420, -1, -1));

        sorteoAddDay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoA.add(sorteoAddDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 420, 30, 30));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(153, 153, 153));
        jLabel15.setText("Día");
        sorteoA.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 390, 40, 30));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(153, 153, 153));
        jLabel21.setText("--");
        sorteoA.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 420, 20, 30));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(153, 153, 153));
        jLabel20.setText("Mes");
        sorteoA.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 390, 40, 30));

        sorteoAddMonth.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoA.add(sorteoAddMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 420, 30, 30));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(153, 153, 153));
        jLabel22.setText("--");
        sorteoA.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 420, 20, 30));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(153, 153, 153));
        jLabel18.setText("Año");
        sorteoA.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 390, 40, 30));

        sorteoAddYear.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sorteoA.add(sorteoAddYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 420, 50, 30));

        btnAddSorteo.setBackground(new java.awt.Color(21, 57, 90));
        btnAddSorteo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnAddSorteo.setForeground(new java.awt.Color(255, 255, 255));
        btnAddSorteo.setText("Agregar sorteo");
        btnAddSorteo.setContentAreaFilled(false);
        btnAddSorteo.setOpaque(true);
        btnAddSorteo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSorteoActionPerformed(evt);
            }
        });
        sorteoA.add(btnAddSorteo, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 480, -1, -1));

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setOpaque(true);
        sorteoA.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1100, 500));

        sorteoPanel.add(sorteoA, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 1200, 590));

        getContentPane().add(sorteoPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 1260, 640));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Lottery (1).jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -60, 1260, 800));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGestionPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGestionPActionPerformed
        sorteoPanel.setVisible(false);
        planPanel.setVisible(true);
        reportPanel.setVisible(false);
        startSorteoPanel.setVisible(false);
        reportInfoPanel.setVisible(false);
    }//GEN-LAST:event_btnGestionPActionPerformed

    private void mouseChangeEnt(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseChangeEnt
        // TODO add your handling code here:
        Color color = new Color(17,46,72);
        evt.getComponent().setBackground(color);
        evt.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_mouseChangeEnt

    private void mouseChangeEx(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseChangeEx
        Color color = new Color(21,57,90);
        evt.getComponent().setBackground(color);
        evt.getComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_mouseChangeEx

    private void btnGestionSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGestionSActionPerformed
        sorteoPanel.setVisible(true);
        planPanel.setVisible(false);
        reportPanel.setVisible(false);
        startSorteoPanel.setVisible(false);
        reportInfoPanel.setVisible(false);
    }//GEN-LAST:event_btnGestionSActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        sorteoPanel.setVisible(false);
        planPanel.setVisible(false);
        reportPanel.setVisible(true);
        startSorteoPanel.setVisible(false);
        reportInfoPanel.setVisible(false);
    }//GEN-LAST:event_btnReportsActionPerformed

    private void delSortBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delSortBActionPerformed
        sorteoA.setVisible(false);
        sorteoM.setVisible(false);
        sorteoE.setVisible(true);
    }//GEN-LAST:event_delSortBActionPerformed

    private void modSortBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modSortBActionPerformed
        sorteoA.setVisible(false);
        sorteoM.setVisible(true);
        sorteoE.setVisible(false);
    }//GEN-LAST:event_modSortBActionPerformed

    private void addSortBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSortBActionPerformed
        sorteoA.setVisible(true);
        sorteoM.setVisible(false);
        sorteoE.setVisible(false);
    }//GEN-LAST:event_addSortBActionPerformed

    private void delPlanBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delPlanBActionPerformed
        planA.setVisible(false);
        planM.setVisible(false);
        planE.setVisible(true);
    }//GEN-LAST:event_delPlanBActionPerformed

    private void addPlanBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPlanBActionPerformed
        // TODO add your handling code here:
        planA.setVisible(true);
        planM.setVisible(false);
        planE.setVisible(false);
    }//GEN-LAST:event_addPlanBActionPerformed

    private void btnAgregarPremioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarPremioActionPerformed
        DefaultTableModel model = (DefaultTableModel) premiosTablaA.getModel();
        if(!premioAddName.getText().trim().equals("") && Validate.checkIfInt(premioAddCant.getText()) && Validate.checkIfInt(premioAddAmount.getText())){
            model.addRow(new Object[]{premioAddName.getText(),premioAddCant.getText(),premioAddAmount.getText()});
        }
        else
            JOptionPane.showMessageDialog(null,"Tanto el premio como la cantidad deben ser numeros enteros positivos", "Error con datos ingresados", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnAgregarPremioActionPerformed

    private void btnEliminarPremioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPremioActionPerformed
        DefaultTableModel model = (DefaultTableModel) premiosTablaA.getModel();
        int selected = premiosTablaA.getSelectedRow();
        if(selected != -1){
            model.removeRow(selected);
        }
    }//GEN-LAST:event_btnEliminarPremioActionPerformed

    private void btnCrearPlanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearPlanActionPerformed
        String nombre = planAddName.getText();
        String sorteo = planAddNum.getText();
        ArrayList<Premio> premios = Premio.getTablePrices(premiosTablaA.getModel());
        Double totalP = Premio.sumTotalPremios(premios);
        String errors = Validate.validatePlan(nombre, sorteo, premios);
        if(errors.equals("")){
            PlanBDManager conector = new PlanBDManager();
            conector.addPlan(nombre, sorteo, premios, BigDecimal.valueOf(totalP).toPlainString());
            fillPlanes();
            fillSorteos();
        }
        else{
            JOptionPane.showMessageDialog(null,errors, "Error en los datos ingresados", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnCrearPlanActionPerformed

    private void btnGenerateReportCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateReportCActionPerformed
        int row = chanceTable.getSelectedRow();
        if(row>=0){
            String idSort = chanceTable.getModel().getValueAt(row, 0).toString();
            String nameSort = chanceTable.getModel().getValueAt(row, 1).toString();
            String typeSort = chanceTable.getModel().getValueAt(row, 2).toString();
            String priceSort = chanceTable.getModel().getValueAt(row, 3).toString();
            String dateSort = chanceTable.getModel().getValueAt(row, 4).toString();
            String idPlan = chanceTable.getModel().getValueAt(row, 5).toString();
            String planTotal = chanceTable.getModel().getValueAt(row, 6).toString();
            reportGenerator(idSort,nameSort,typeSort,priceSort,dateSort,idPlan,planTotal);
            reportPanel.setVisible(false);
            reportInfoPanel.setVisible(true);
        }
        else{
            JOptionPane.showMessageDialog(null,"Se debe seleccionar un sorteo de la tabla para jugar", "Error de seleccion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnGenerateReportCActionPerformed

    private void btnGenerateReportLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateReportLActionPerformed
        int row = loteriaTable.getSelectedRow();
        if(row>=0){
            String idSort = loteriaTable.getModel().getValueAt(row, 0).toString();
            String nameSort = loteriaTable.getModel().getValueAt(row, 1).toString();
            String typeSort = loteriaTable.getModel().getValueAt(row, 2).toString();
            String priceSort = loteriaTable.getModel().getValueAt(row, 3).toString();
            String dateSort = loteriaTable.getModel().getValueAt(row, 4).toString();
            String idPlan = loteriaTable.getModel().getValueAt(row, 5).toString();
            String planTotal = loteriaTable.getModel().getValueAt(row, 6).toString();
            reportGenerator(idSort,nameSort,typeSort,priceSort,dateSort,idPlan,planTotal);
            reportPanel.setVisible(false);
            reportInfoPanel.setVisible(true);
        }
        else{
            JOptionPane.showMessageDialog(null,"Se debe seleccionar un sorteo de la tabla para jugar", "Error de seleccion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnGenerateReportLActionPerformed

    private void btnStartGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartGActionPerformed
        sorteoPanel.setVisible(false);
        planPanel.setVisible(false);
        reportPanel.setVisible(false);
        startSorteoPanel.setVisible(true);
        reportInfoPanel.setVisible(false);
    }//GEN-LAST:event_btnStartGActionPerformed

    private void btnPlayGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayGameActionPerformed
        infoMess.setVisible(false);
        int row = sorteoPlayTable.getSelectedRow();
        if(row>=0){
            panelInGame.setVisible(true);
            btnPlayGame.setEnabled(false);
            startGame(row);
            fillSorteos();
        }
        else{
            JOptionPane.showMessageDialog(null,"Se debe seleccionar un sorteo de la tabla para jugar", "Error de seleccion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnPlayGameActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        InicioAdmin secondForm = new InicioAdmin(2);
        secondForm.show();

        this.dispose();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void btnAddSorteoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSorteoActionPerformed
        sorteoAddErr.setText("");
        String errors = Validate.validateSorteo(sorteoAddName.getText(),sorteoAddType.getSelectedItem().toString(),sorteoAddDay.getText(), sorteoAddMonth.getText(), sorteoAddYear.getText(),sorteoAddPrice.getText(),sorteoAddFracc.getText());
        if(errors.equals("")){
            String date = sorteoAddYear.getText()+"-"+ sorteoAddMonth.getText()+"-"+ sorteoAddDay.getText();
            SorteoBDManager connector = new SorteoBDManager();
            connector.addSorteo(sorteoAddName.getText(), date, sorteoAddType.getSelectedItem().toString(), sorteoAddPrice.getText(), "1", sorteoAddFracc.getText());
            fillSorteos();
        }
        else{
            JOptionPane.showMessageDialog(null,errors, "Error en los datos ingresados", JOptionPane.INFORMATION_MESSAGE);
            //sorteoAddErr.setText("Error Date");
        }
    }//GEN-LAST:event_btnAddSorteoActionPerformed

    private void btnDeleteSorteoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSorteoActionPerformed
        int column = 0;
        int row = sorteoTablaE.getSelectedRow();
        if(row>=0){
            String value = sorteoTablaE.getModel().getValueAt(row, column).toString();
            SorteoBDManager connector = new SorteoBDManager();
            connector.deleteSorteo(value);
            fillSorteos();
        }
        else{
            JOptionPane.showMessageDialog(null,"Se debe seleccionar un sorteo de la tabla para eliminar", "Error de seleccion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteSorteoActionPerformed

    private void btnModSorteoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModSorteoActionPerformed
        String errors = Validate.validateSorteo(sorteoModName.getText(),sorteoModType.getText(),sorteoModDay.getText(), sorteoModMonth.getText(), sorteoModYear.getText(),sorteoModPrice.getText(),sorteoModFracc.getText());
        if(errors.equals("")){
            String date = sorteoModYear.getText()+"-"+ sorteoModMonth.getText()+"-"+ sorteoModDay.getText();
            int column = 0;
            int row = sorteoTablaM.getSelectedRow();
            if(row>=0){
                String value = sorteoTablaM.getModel().getValueAt(row, column).toString();
                SorteoBDManager connector = new SorteoBDManager();
                connector.modifySorteo(value,sorteoModName.getText(), date, sorteoModType.getText(), sorteoModPrice.getText(), sorteoModFracc.getText());
                fillSorteos();
            }
            else{
                JOptionPane.showMessageDialog(null,"No se tiene seleccionado un sorteo", "Error de seleccion", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else{
            JOptionPane.showMessageDialog(null,errors, "Error en los datos ingresados", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnModSorteoActionPerformed

    private void btnSelectSorteoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectSorteoActionPerformed
        int row = planSorteoTablaA.getSelectedRow();
        if(row>=0){
            String num = planSorteoTablaA.getValueAt(row, 0).toString();
            planAddNum.setText(num);
        }
    }//GEN-LAST:event_btnSelectSorteoActionPerformed

    private void btnEliminarPlanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPlanActionPerformed
        int column = 0;
        int row = planesTablaE.getSelectedRow();
        if(row>=0){
            String value = planesTablaE.getModel().getValueAt(row, column).toString();
            String sorteo = planesTablaE.getModel().getValueAt(row, 1).toString();
            PlanBDManager connector = new PlanBDManager();
            connector.deletePlan(value,sorteo);
            fillPlanes();
            fillSorteos();
        }
        else{
            JOptionPane.showMessageDialog(null,"Seleccione un sorteo de la tabla para eliminar", "Error de seleccion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnEliminarPlanActionPerformed

    private void btnSelectSorteoMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectSorteoMActionPerformed
        int row = planSorteoTablaM.getSelectedRow();
        if(row>=0){
            String num = planSorteoTablaM.getValueAt(row, 0).toString();
            planModNum.setText(num);
        }
    }//GEN-LAST:event_btnSelectSorteoMActionPerformed

    private void btnAgregarPremioMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarPremioMActionPerformed
        DefaultTableModel model = (DefaultTableModel) premiosTablaM.getModel();
        if(!premioAddNameM.getText().trim().equals("") && Validate.checkIfInt(premioAddCantM.getText()) && Validate.checkIfInt(premioAddAmountM.getText())){
            model.addRow(new Object[]{premioAddNameM.getText(),premioAddCantM.getText(),premioAddAmountM.getText()});
        }
        else
            JOptionPane.showMessageDialog(null,"Tanto el premio como la cantidad deben ser numeros enteros positivos", "Error con datos ingresados", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnAgregarPremioMActionPerformed

    private void btnEliminarPremioMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPremioMActionPerformed
        DefaultTableModel model = (DefaultTableModel) premiosTablaM.getModel();
        int selected = premiosTablaM.getSelectedRow();
        if(selected != -1){
            model.removeRow(selected);
        }
    }//GEN-LAST:event_btnEliminarPremioMActionPerformed

    private void btnModPlanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModPlanActionPerformed
        String nombre = planModName.getText();
        String sorteo = planModNum.getText();
        String oldSorteo = planModOldNum.getText();
        ArrayList<Premio> premios = Premio.getTablePrices(premiosTablaM.getModel());
        Double totalP = Premio.sumTotalPremios(premios);
        String errors = Validate.validatePlanM(nombre, sorteo, premios);
        if(errors.equals("")){
            PlanBDManager conector = new PlanBDManager();
            conector.updatePlan(nombre, sorteo,oldSorteo, premios,BigDecimal.valueOf(totalP).toPlainString());
            fillPlanes();
            fillSorteos();
            planM.setVisible(false);
            planE.setVisible(true);
        }
        else{
            JOptionPane.showMessageDialog(null,errors, "Error en los datos ingresados", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnModPlanActionPerformed

    private void btnGoModPlanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoModPlanActionPerformed
        int column = 0;
        int row = planesTablaE.getSelectedRow();
        if(row>=0){
            String name = planesTablaE.getModel().getValueAt(row, column).toString();
            String numSorteo = planesTablaE.getModel().getValueAt(row, 1).toString();
            planModName.setText(name);
            planModNum.setText(numSorteo);
            planModOldNum.setText(numSorteo);
            planE.setVisible(false);
            planM.setVisible(true);
        }
        else{
            JOptionPane.showMessageDialog(null,"Se debe seleccionar un plan para modificar en la tabla", "Error de seleccion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnGoModPlanActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        reportPanel.setVisible(true);
        reportInfoPanel.setVisible(false);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void btnEndGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndGameActionPerformed
        panelInGame.setVisible(false);
        btnEndGame.setVisible(false);
        btnPlayGame.setEnabled(true);
        gameNum.setText("0");
        gameSer.setText("0");
        gamePrem.setText("0");
        infoMess.setVisible(true);
        DefaultTableModel model = (DefaultTableModel) winnerTable.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_btnEndGameActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminSide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminSide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminSide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminSide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminSide().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPlanB;
    private javax.swing.JButton addSortB;
    private javax.swing.JLabel bolaLot;
    private javax.swing.JButton btnAddSorteo;
    private javax.swing.JButton btnAgregarPremio;
    private javax.swing.JButton btnAgregarPremioM;
    private javax.swing.JButton btnCrearPlan;
    private javax.swing.JButton btnDeleteSorteo;
    private javax.swing.JButton btnEliminarPlan;
    private javax.swing.JButton btnEliminarPremio;
    private javax.swing.JButton btnEliminarPremioM;
    private javax.swing.JButton btnEndGame;
    private javax.swing.JButton btnGenerateReportC;
    private javax.swing.JButton btnGenerateReportL;
    private javax.swing.JButton btnGestionP;
    private javax.swing.JButton btnGestionS;
    private javax.swing.JButton btnGoModPlan;
    private javax.swing.JButton btnModPlan;
    private javax.swing.JButton btnModSorteo;
    private javax.swing.JButton btnPlayGame;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSelectSorteo;
    private javax.swing.JButton btnSelectSorteoM;
    private javax.swing.JButton btnStartG;
    private javax.swing.JTable chanceTable;
    private javax.swing.JButton delPlanB;
    private javax.swing.JButton delSortB;
    private javax.swing.JLabel gameInfo;
    private javax.swing.JLabel gameNum;
    private javax.swing.JLabel gamePrem;
    private javax.swing.JLabel gameSer;
    private javax.swing.JLabel infoMess;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton13;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable loteriaTable;
    private javax.swing.JButton modSortB;
    private javax.swing.JPanel panelInGame;
    private javax.swing.JPanel planA;
    private javax.swing.JTextField planAddName;
    private javax.swing.JLabel planAddNum;
    private javax.swing.JPanel planE;
    private javax.swing.JPanel planM;
    private javax.swing.JLabel planModName;
    private javax.swing.JLabel planModNum;
    private javax.swing.JLabel planModOldNum;
    private javax.swing.JPanel planPanel;
    private javax.swing.JTable planSorteoTablaA;
    private javax.swing.JTable planSorteoTablaM;
    private javax.swing.JTable planesTablaE;
    private javax.swing.JTextField premioAddAmount;
    private javax.swing.JTextField premioAddAmountM;
    private javax.swing.JTextField premioAddCant;
    private javax.swing.JTextField premioAddCantM;
    private javax.swing.JTextField premioAddName;
    private javax.swing.JTextField premioAddNameM;
    private javax.swing.JTable premiosTablaA;
    private javax.swing.JTable premiosTablaE;
    private javax.swing.JTable premiosTablaM;
    private javax.swing.JLabel reportCode;
    private javax.swing.JLabel reportDate;
    private javax.swing.JPanel reportInfoPanel;
    private javax.swing.JLabel reportName;
    private javax.swing.JPanel reportPanel;
    private javax.swing.JLabel reportPlan;
    private javax.swing.JTable reportPremioTable;
    private javax.swing.JLabel reportPrice;
    private javax.swing.JLabel reportTitle;
    private javax.swing.JLabel reportTotal;
    private javax.swing.JLabel reportType;
    private javax.swing.JTable reportWinnerTable;
    private javax.swing.JPanel sorteoA;
    private javax.swing.JTextField sorteoAddDay;
    private javax.swing.JLabel sorteoAddErr;
    private javax.swing.JTextField sorteoAddFracc;
    private javax.swing.JTextField sorteoAddMonth;
    private javax.swing.JTextField sorteoAddName;
    private javax.swing.JTextField sorteoAddPrice;
    private javax.swing.JComboBox<String> sorteoAddType;
    private javax.swing.JTextField sorteoAddYear;
    private javax.swing.JPanel sorteoE;
    private javax.swing.JPanel sorteoM;
    private javax.swing.JTextField sorteoModDay;
    private javax.swing.JTextField sorteoModFracc;
    private javax.swing.JTextField sorteoModMonth;
    private javax.swing.JTextField sorteoModName;
    private javax.swing.JTextField sorteoModPrice;
    private javax.swing.JLabel sorteoModType;
    private javax.swing.JTextField sorteoModYear;
    private javax.swing.JPanel sorteoPanel;
    private javax.swing.JTable sorteoPlayTable;
    private javax.swing.JTable sorteoTablaA;
    private javax.swing.JTable sorteoTablaE;
    private javax.swing.JTable sorteoTablaM;
    private javax.swing.JPanel startSorteoPanel;
    private javax.swing.JTable winnerTable;
    // End of variables declaration//GEN-END:variables
}
