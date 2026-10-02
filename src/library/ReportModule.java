package library;

import java.util.ArrayList;

/** MODULE 3 (Report/Management): search, delete, generate reports. */
public class ReportModule {
    private final Library lib;

    public ReportModule(Library lib) { this.lib = lib; }

    public ArrayList<LibraryItem> search(String keyword) {
        ArrayList<LibraryItem> found = new ArrayList<>();
        for (LibraryItem i : lib.getItems())
            if ((i.getTitle() + " " + i.getDetails()).toLowerCase().contains(keyword.trim().toLowerCase()))
                found.add(i);
        return found;
    }

    public boolean deleteItem(String id) {
        for (LibraryItem i : lib.getItems())
            if (i.getId().equalsIgnoreCase(id)) return i.isAvailable() && lib.getItems().remove(i);
        return false;
    }

    public String summary() {
        int out = 0, members = 0;
        for (LibraryItem i : lib.getItems()) if (!i.isAvailable()) out++;
        for (User u : lib.getUsers()) if (u instanceof Member) members++;
        double rent = 0, sales = 0, fines = 0, unpaid = 0;
        for (Payment p : lib.getPayments()) {
            if (!p.isPaid()) unpaid += p.getAmount();
            else if (p.getType().equals("Rental")) rent += p.getAmount();
            else if (p.getType().equals("Purchase")) sales += p.getAmount();
            else fines += p.getAmount();
        }
        int total = lib.getItems().size();
        return "LIBROLINK REPORT\n----------------\nItems in library : " + total
                + "\nAvailable        : " + (total - out) + "\nBorrowed         : " + out
                + "\nMembers          : " + members
                + "\nRental income    : PHP " + String.format("%.2f", rent)
                + "\nBook sales       : PHP " + String.format("%.2f", sales)
                + "\nLate fines paid  : PHP " + String.format("%.2f", fines)
                + "\nUnpaid fines     : PHP " + String.format("%.2f", unpaid)
                + "\nTOTAL INCOME     : PHP " + String.format("%.2f", rent + sales + fines);
    }
}
