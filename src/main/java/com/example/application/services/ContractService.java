package com.example.application.services;

import com.example.application.dao.implementations.ContractDAO;
import com.example.application.dto.ClientDTO;
import com.example.application.dto.ContractDTO;
import com.example.application.dto.PlanDTO;
import com.example.application.models.ContractsEntity;
import com.example.application.models.OptionsEntity;
import com.example.application.models.PlansEntity;
import com.example.application.validation.ContractValidation;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final DozerBeanMapper mapper;

    private final ContractDAO contractDAO;

    private final ClientsService clientsService;

    private final PlansService plansService;

    private final OptionsService optionsService;

    private final ContractValidation contractValidation;

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<ContractDTO> getContractsList() {
        return contractDAO.findAll().stream()
                .map(entity -> mapper.map(entity, ContractDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public ContractDTO getContract(int id) {
        ContractsEntity contract = contractDAO.show(id);
        return mapper.map(contract, ContractDTO.class);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createContract(ContractDTO contract) {
        ClientDTO client = clientsService.getClient(contract.getClient().getId());
        PlansEntity plan = mapper.map(plansService.getPlan(contract.getPlan().getId()), PlansEntity.class);
        contract.setClient(client);
        contract.setPlan(mapper.map(plan, PlanDTO.class));
        contractDAO.add(mapper.map(contract, ContractsEntity.class));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteContract(int id) {
        ContractsEntity contract = contractDAO.show(id);
        contractDAO.delete(contract);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateContract(ContractDTO contract) {
        ClientDTO client = clientsService.getClient(contract.getClient().getId());
        PlansEntity plan = mapper.map(plansService.getPlan(contract.getPlan().getId()), PlansEntity.class);
        contract.setClient(client);
        contract.setPlan(mapper.map(plan, PlanDTO.class));
        if (!contractValidation.validateContract(mapper.map(contract, ContractsEntity.class))) {
            throw new RuntimeException("Failed to create contract, contract is invalid");
        }
        contractDAO.edit(mapper.map(contract, ContractsEntity.class));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateContract(int id, Set<OptionsEntity> options, PlansEntity plan) {
        ContractsEntity contract = contractDAO.show(id);
        contract.setOptions(new HashSet<OptionsEntity>());
        for (OptionsEntity opt : options) {
            contract.getOptions().add(mapper.map(optionsService.getOption(opt.getId()), OptionsEntity.class));
        }
        contract.setPlan(mapper.map(plansService.getPlan(plan.getId()), PlansEntity.class));
        if (!contractValidation.validateContract(contract)) {
            throw new RuntimeException("Failed to create contract, contract is invalid");
        }
        contractDAO.edit(contract);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void blockContract(int id, boolean isBlocked, boolean isBlockedByManager) {
        ContractsEntity contract = contractDAO.show(id);
        contract.setIsBlocked(isBlocked);
        contract.setIsBlockedByManager(isBlockedByManager);
        contractDAO.edit(contract);
    }
}
