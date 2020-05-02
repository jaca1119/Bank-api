package com.itvsme.bank.transfer;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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

    @GetMapping("/account/{accountId}/transfers")
    public ResponseEntity<Page<TransferDTO>> getAccountTransfersPage(@PathVariable Integer accountId, Principal principal)
    {
        return ResponseEntity.ok(transferService.getAccountTransfersPage(accountId, principal));
    }
}
