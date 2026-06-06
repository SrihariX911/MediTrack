package com.airtribe.meditrack.patterns;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.util.IdGenerator;

/**
 * Factory for creating Bill instances using different billing strategies.
 * Demonstrates: Factory pattern + Singleton (eager instance).
 */
public class BillFactory {

    // Eager singleton — initialised when class is first loaded
    private static final BillFactory INSTANCE = new BillFactory();

    private BillFactory() {}

    public static BillFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a bill by applying the supplied strategy to the appointment's doctor fee.
     */
    public Bill createBill(Appointment appointment, BillingStrategy strategy) {
        double fee  = appointment.getDoctor().getConsultationFee();
        double base = strategy.calculateBase(fee);
        double tax  = strategy.calculateTax(base);
        String id   = IdGenerator.getInstance().generateBillId();
        return new Bill(id, appointment, base, tax, strategy.getName());
    }

    /** Shortcut — standard billing. */
    public Bill createStandardBill(Appointment appointment) {
        return createBill(appointment, new StandardBillingStrategy());
    }

    /** Shortcut — senior citizen discount billing. */
    public Bill createSeniorBill(Appointment appointment) {
        return createBill(appointment, DiscountedBillingStrategy.senior());
    }

    /** Shortcut — general discount billing. */
    public Bill createDiscountedBill(Appointment appointment) {
        return createBill(appointment, DiscountedBillingStrategy.general());
    }
}
