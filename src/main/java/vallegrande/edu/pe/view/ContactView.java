package vallegrande.edu.pe.view;

import vallegrande.edu.pe.controller.ContactController;
import vallegrande.edu.pe.model.Contact;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Vista principal con Swing que muestra la lista de contactos y botones.
 * Ventana maximizada, botones con estilo moderno y colores.
 */
public class ContactView extends JFrame {
    private final ContactController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    // Colores modernos
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255); // Azul
    private static final Color DANGER_COLOR = new Color(220, 53, 69);  // Rojo
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Gris claro
    private static final Color FOREGROUND_COLOR = new Color(51, 51, 51); // Gris oscuro

    public ContactView(ContactController controller) {
        super("Agenda MVC Swing - Vallegrande");
        this.controller = controller;
        initUI();
        loadContacts();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Fuente base
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 16);

        // Panel principal con estilo
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15)); // Aumentamos el espacio
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR); // Fondo gris claro
        setContentPane(contentPanel);

        // Tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Teléfono"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable(table, baseFont); // Nuevo método para estilizar la tabla

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonsPanel.setBackground(BACKGROUND_COLOR); // Fondo gris claro

        // Botón "Agregar"
        JButton addBtn = new JButton("Agregar");
        styleButton(addBtn, PRIMARY_COLOR);

        // Botón "Eliminar"
        JButton deleteBtn = new JButton("Eliminar");
        styleButton(deleteBtn, DANGER_COLOR);

        buttonsPanel.add(addBtn);
        buttonsPanel.add(deleteBtn);

        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Eventos
        addBtn.addActionListener(e -> showAddContactDialog());
        deleteBtn.addActionListener(e -> deleteSelectedContact());
    }

    /**
     * Aplica estilos modernos a la tabla.
     */
    private void styleTable(JTable table, Font baseFont) {
        table.setFont(baseFont);
        table.setRowHeight(35); // Un poco más de altura para un mejor tacto visual
        table.setForeground(FOREGROUND_COLOR);
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220)); // Ligeras líneas de la cuadrícula
        table.getTableHeader().setFont(baseFont.deriveFont(Font.BOLD, 18f));
        table.getTableHeader().setBackground(new Color(230, 230, 230)); // Cabecera con fondo gris
        table.getTableHeader().setForeground(FOREGROUND_COLOR);
        table.getTableHeader().setReorderingAllowed(false);
    }

    /**
     * Aplica estilos modernos y hover a los botones.
     */
    private void styleButton(JButton button, Color baseColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25)); // Aumentamos el padding
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
    }

    // (Los métodos loadContacts, showAddContactDialog, deleteSelectedContact permanecen sin cambios)
    private void loadContacts() {
        tableModel.setRowCount(0);
        List<Contact> contacts = controller.list();
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{c.id(), c.name(), c.email(), c.phone()});
        }
    }

    private void showAddContactDialog() {
        AddContactDialog dialog = new AddContactDialog(this, controller);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadContacts();
        }
    }

    private void deleteSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este contacto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadContacts();
        }
    }
}