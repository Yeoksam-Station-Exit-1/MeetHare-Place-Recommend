package yeoksamstationexit1.recommend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeoksamstationexit1.recommend.dto.PriorityDTO;
import yeoksamstationexit1.recommend.entity.Priority;
import yeoksamstationexit1.recommend.repository.PriorityRepository;
import yeoksamstationexit1.recommend.util.DataNotFoundException;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class PriorityService {

    private final PriorityRepository priorityRepository;

    public Integer createPriority(PriorityDTO priorityDTO) {
        Optional<Priority> optionalPriority = priorityRepository.findById(priorityDTO.getUserID());
        if (optionalPriority.isPresent()) return 0;
        else {
            Priority priority = priorityDTO.toPriority();
            priorityRepository.save(priority);
            return 1;
        }
    }

    public void updatePriority(PriorityDTO priorityDTO) throws DataNotFoundException {
        Optional<Priority> optionalPriority = priorityRepository.findById(priorityDTO.getUserID());
        if (optionalPriority.isEmpty()) throw new DataNotFoundException("User Not Found");
        else {
            optionalPriority.get().update(priorityDTO);
            priorityRepository.save(optionalPriority.get());
        }
    }

    public void deletePriority(Long userId) throws DataNotFoundException {
        Optional<Priority> optionalPriority = priorityRepository.findById(userId);
        if (optionalPriority.isEmpty()) throw new DataNotFoundException("User Not Found");
        else priorityRepository.delete(optionalPriority.get());
    }

    public PriorityDTO getPriority(Long userNum) throws DataNotFoundException {
        Optional<Priority> optionalPriority = priorityRepository.findById(userNum);
        if (optionalPriority.isEmpty()) throw new DataNotFoundException("User Not Found");
        else return new PriorityDTO(optionalPriority.get());
    }
}
