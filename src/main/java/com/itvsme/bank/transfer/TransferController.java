package com.itvsme.bank.transfer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController
{
    private TransferService transferService;

    public TransferController(TransferService transferService)
    {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> makeTransfer(@RequestBody TransferDTO transferDTO)
    {
        if (transferService.makeTransfer(transferDTO))
        {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("Transfer");
    }
}
