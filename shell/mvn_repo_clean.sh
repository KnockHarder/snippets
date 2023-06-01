#!/bin/bash
 
suffix=("SNAPSHOT" "Final" "android" "jre" "RELEASE")

function clean_old_files(){
  local base=$1

  local releases=($(ls $base | grep -E '^[0-9.]+$' | sort -hr))
  for i in ${releases[@]:1}
  do
    echo rm $base/$i          # log
    rm -rf $base/$i
  done
  
  for s in ${suffix[@]}
  do
    li=($(ls $base | grep -E '^[0-9.-]+'$s'$' | sort -hr -t'.' -k1,1 -k2,2 -k3,3 -k4,4))
    for i in ${li[@]:1}
    do
      echo rm $base/$i          # log
      rm -rf $base/$i
    done
  done

  local dirs=($(ls $base | grep -E -v '^[0-9.]+'))
  local versionPrefix=($(ls $base | grep -E '^[0-9.]+|[0-9.]+$'))
  if [ ${#versionPrefix[@]} -gt 0 -a ${#dir[@]} -gt 0 ]
  then
    echo May miss: $(ls $base)
  fi

  for i in ${dirs[@]}
  do
    if [ -d $base/$i ]
    then
      clean_old_files $base/$i
    fi
  done
}

clean_old_files $HOME/.m2/repository
