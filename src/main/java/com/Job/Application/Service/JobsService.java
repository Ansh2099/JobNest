package com.Job.Application.Service;

import com.Job.Application.Model.Jobs;
import com.Job.Application.Repo.JobsRepo;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobsService {

    @Autowired
    private JobsRepo repo;

    public List<Jobs> getAllJobs() {
        return repo.findAll();
    }

    public Jobs postJob(Jobs jobs) {
        return repo.save(jobs);
    }

    public void deleteJob(Long id) {


    }

    public Jobs getJobById(long id) {
        return repo.findById(id).orElse(null);
    }
}
