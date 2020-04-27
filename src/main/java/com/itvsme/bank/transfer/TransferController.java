package com.itvsme.bank.transfer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController
{
    @PostMapping("/transfer")
    public ResponseEntity<String> makeTransfer()
    {
        return ResponseEntity.ok("Transfer");
    }
}
