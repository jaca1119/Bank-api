package com.itvsme.bank.transfer;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.ok("Transfer");
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/transfers/account/{id}")
    public ResponseEntity<Page<TransferDTO>> getAccountTransfersPage(@PathVariable Integer id)
    {
        return ResponseEntity.ok(transferService.getAccountTransfersPage(id));
    }
}
