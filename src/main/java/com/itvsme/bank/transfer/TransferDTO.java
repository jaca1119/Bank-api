package com.itvsme.bank.transfer;

import java.sql.Timestamp;

public class TransferDTO
{
    private String from;
    private String to;

    private long amountInHundredScale;

    private Timestamp transferDateTime;
    private String zoneId;

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public long getAmountInHundredScale()
    {
        return amountInHundredScale;
    }

    public void setAmountInHundredScale(long amountInHundredScale)
    {
        this.amountInHundredScale = amountInHundredScale;
    }

    public Timestamp getTransferDateTime()
    {
        return transferDateTime;
    }

    public void setTransferDateTime(Timestamp transferDateTime)
    {
        this.transferDateTime = transferDateTime;
    }

    public String getZoneId()
    {
        return zoneId;
    }

    public void setZoneId(String zoneId)
    {
        this.zoneId = zoneId;
    }
}
